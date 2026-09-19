package com.corebank.tests;

import com.corebank.pages.LoginPage;
import com.corebank.pages.TransferPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
        return new Object[][] {
                { "1999", "Lỗi: Số tiền dưới mức tối thiểu 2000 VND" },
                { "5000000", "Chuyển thành công (Dưới 10tr - Không cần FaceID)" },
                { "10000000", "Chuyển thành công (Chạm mốc 10tr - Bật FaceID)" },
                { "300000001", "Lỗi: Vượt hạn mức 300tr của tài khoản Standard" }
        };
    }

    // --- TIÊM DATA PROVIDER VÀO HÀM TEST ---
    @Test(dataProvider = "limitTestData")
    public void testTransferLimits(String amountToTransfer, String expectedBehavior) throws InterruptedException {
        System.out.println("\n--- [UI] ĐANG TEST KỊCH BẢN: " + expectedBehavior + " với số tiền: " + amountToTransfer + "$ ---");

        LoginPage loginPage = new LoginPage(driver);
        TransferPage transferPage = new TransferPage(driver);

        // 1. Đăng nhập
        loginPage.openPage();
        loginPage.login(myUser, myPass);

        // 2. Chuyển tiền
        transferPage.goToTransferFunds();
        transferPage.transferMoney(amountToTransfer, accountId1, accountId2);

        // 3. Kiểm thử (Assert)
        Assert.assertTrue(transferPage.isTransferComplete(), "Lỗi: Chuyển tiền thất bại tại kịch bản " + expectedBehavior);
        System.out.println("[UI] Pass kịch bản: " + expectedBehavior);

        // 4. ĐĂNG XUẤT (Để dọn dẹp trình duyệt cho lần chạy DataProvider tiếp theo)
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