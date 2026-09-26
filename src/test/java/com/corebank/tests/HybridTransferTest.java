package com.corebank.tests;

import com.corebank.pages.LoginPage;
import com.corebank.pages.TransferPage;
import com.corebank.utils.ExcelUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;

public class HybridTransferTest {
    // Data cho API
    private String myUser = "john";
    private String myPass = "demo";
    private int customerId;
    private String accountId1;
    private String accountId2;

    // Biến điều khiển Trình duyệt Web
    private WebDriver driver;

    @BeforeClass
    public void setupDataViaAPI() {
        System.out.println("--- [API] ĐANG LÀM GIÁN ĐIỆP CHUẨN BỊ DỮ LIỆU ---");

        // 1. Bắn API Login lấy Customer ID
        Response loginRes = RestAssured.given()
                .accept(io.restassured.http.ContentType.JSON)
                .get("https://parabank.parasoft.com/parabank/services/bank/login/" + myUser + "/" + myPass);
        customerId = loginRes.jsonPath().getInt("id");

        // 2. Bắn API Lấy tài khoản gốc
        Response accountsRes = RestAssured.given()
                .accept(io.restassured.http.ContentType.JSON)
                .get("https://parabank.parasoft.com/parabank/services/bank/customers/" + customerId + "/accounts");
        accountId1 = accountsRes.jsonPath().getString("[0].id");

        // 3. Bắn API Tạo tài khoản mới tinh
        Response newAccRes = RestAssured.given()
                .accept(io.restassured.http.ContentType.JSON)
                .post("https://parabank.parasoft.com/parabank/services/bank/createAccount?customerId="
                        + customerId + "&newAccountType=1&fromAccountId=" + accountId1);
        accountId2 = newAccRes.jsonPath().getString("id");

        System.out.println("[API] Xong! TK Nguồn: " + accountId1 + " | TK Nhận mới tạo: " + accountId2);

        // 4. Khởi động Webdriver với Cấu hình Thông minh (Smart Config)
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        // Phát hiện nếu đang chạy trên máy chủ CI/CD (GitHub) thì Tàng hình (Headless)
        if (System.getenv("GITHUB_ACTIONS") != null) {
            options.addArguments("--headless=new");
        } else {
            // Còn chạy ở máy bạn (Local) thì mở to màn hình để xem cho rõ
            options.addArguments("--start-maximized");
        }

        driver = new ChromeDriver(options);
    }

    // --- KHAI BÁO DATA PROVIDER (Các ranh giới nguy hiểm) ---
    @DataProvider(name = "limitTestData")
    public Object[][] getLimitData() {
        // user.dir tự động lấy đường dẫn root của project (Chạy trên Windows, Mac hay Linux GitHub đều đúng)
        String filepath = System.getProperty("user.dir") + "/src/test/resources/testdata/TransferData.xlsx";
        String sheetname = "Sheet1";

        return ExcelUtils.getTestData(filepath, sheetname);
    }

    @Test(dataProvider = "limitTestData")
    public void testTransferLimits(String testCaseName, String amountToTransfer, String expectedBehavior) {
        System.out.println("Đang chạy kịch bản: " + testCaseName);
        System.out.println("\n--- [UI] ĐANG TEST KỊCH BẢN: " + expectedBehavior + " với số tiền: " + amountToTransfer + "$ ---");

        LoginPage loginPage = new LoginPage(driver);
        TransferPage transferPage = new TransferPage(driver);

        loginPage.openPage();
        loginPage.login(myUser, myPass);

        transferPage.goToTransferFunds(accountId1);
        transferPage.transferMoney(amountToTransfer, accountId1, accountId2);

        // Chú ý: Phải truyền tham số expectedBehavior vào hàm này
        // Truyền câu mong đợi vào để hàm kia biết đường mà "đợi"
        String actualMessage = transferPage.getResultMessage(expectedBehavior);

        // Assert kiểm tra chứa đựng
        Assert.assertTrue(actualMessage.contains(expectedBehavior),
                "Lỗi: Không tìm thấy câu '" + expectedBehavior + "' trên màn hình!");} // <-- ĐÓNG NGOẶC CHO HÀM @Test Ở ĐÂY

    // TẠO HÀM @AfterMethod NGANG HÀNG BÊN DƯỚI
    @AfterMethod
    public void cleanUpLession() {
        // Hàm này sẽ ĐẢM BẢO luôn Logout dù Test Pass hay Fail
        driver.get("https://parabank.parasoft.com/parabank/logout.htm");
    }
    @AfterClass
    public void teardown() {
        System.out.println("\n--- [TEARDOWN] Dọn dẹp chiến trường ---");

        // Dùng API làm "Bút toán đảo": Trả tổng số tiền về chỗ cũ để cân bằng sổ sách
        RestAssured.given()
                .post("https://parabank.parasoft.com/parabank/services/bank/transfer?fromAccountId="
                        + accountId2 + "&toAccountId=" + accountId1 + "&amount=315001999");
        System.out.println("[API] Đã thực hiện Bút toán đảo để cân bằng sổ sách!");

        if (driver != null) {
            driver.quit();
        }
    }
}