package com.corebank.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ParabankRealTest {

    private String myUser = "john";
    private String myPass = "demo";

    private int customerId;
    private int accountId1;
    private int accountId2;

    @BeforeClass
    public void prepareAccounts() {
        // [BẬT CAMERA]: Dòng này sẽ đính kèm tự động mọi Request/Response vào Allure Report
        RestAssured.filters(new io.qameta.allure.restassured.AllureRestAssured());

        System.out.println("\n--- [TIỀN TRẠM] Lấy thông tin tài khoản ---");
        // ... (Giữ nguyên phần code lấy ID bên dưới của bạn) ...
        // 1. Đăng nhập để lấy Customer ID
        Response loginRes = RestAssured.given()
                .accept(ContentType.JSON)
                .get("https://parabank.parasoft.com/parabank/services/bank/login/" + myUser + "/" + myPass);

        // [CHỐT CHẶN]: Kiểm tra đăng nhập thành công trước khi Parse JSON
        Assert.assertEquals(loginRes.statusCode(), 200, "LỖI SETUP: Đăng nhập thất bại! Chi tiết: " + loginRes.asString());
        customerId = loginRes.jsonPath().getInt("id");
        System.out.println("-> Customer ID của bạn: " + customerId);

        // 2. Lấy danh sách tài khoản ngân hàng của bạn
        Response accRes = RestAssured.given()
                .accept(ContentType.JSON)
                .get("https://parabank.parasoft.com/parabank/services/bank/customers/" + customerId + "/accounts");

        // [CHỐT CHẶN]: Đảm bảo gọi danh sách tài khoản thành công
        Assert.assertEquals(accRes.statusCode(), 200, "LỖI SETUP: Không lấy được danh sách tài khoản!");
        accountId1 = accRes.jsonPath().getInt("[0].id");
        System.out.println("-> Tài khoản gốc (Account 1): " + accountId1);

        // 3. Mở thêm 1 tài khoản Tiết kiệm (Savings) để nhận tiền
        Response newAccRes = RestAssured.given()
                .accept(ContentType.JSON)
                .post("https://parabank.parasoft.com/parabank/services/bank/createAccount?customerId="
                        + customerId + "&newAccountType=1&fromAccountId=" + accountId1);

        // [CHỐT CHẶN]: Đảm bảo mở thẻ thành công
        Assert.assertEquals(newAccRes.statusCode(), 200, "LỖI SETUP: Không tạo được tài khoản mới!");
        accountId2 = newAccRes.jsonPath().getInt("id");
        System.out.println("-> Vừa mở thêm Tài khoản phụ (Account 2): " + accountId2);
    }

    @Test(description = "Test tính năng Chuyển tiền (Transfer Funds)")
    public void testMoneyTransfer() {
        System.out.println("\n--- [TEST] THỰC HIỆN CHUYỂN TIỀN ---");
        int transferAmount = 50;

        System.out.println("-> Đang chuyển $" + transferAmount + " từ TK " + accountId1 + " sang TK " + accountId2);

        Response transferRes = RestAssured.given()
                .accept(ContentType.JSON)
                .post("https://parabank.parasoft.com/parabank/services/bank/transfer?fromAccountId="
                        + accountId1 + "&toAccountId=" + accountId2 + "&amount=" + transferAmount);

        // [CHỐT CHẶN]: Khẳng định API trả về 200 Thành công
        Assert.assertEquals(transferRes.statusCode(), 200, "LỖI TEST: Chuyển tiền thất bại!");

        String resultMsg = transferRes.getBody().asString();
        System.out.println("-> Kết quả: " + resultMsg);
    }

    @AfterMethod
    public void revertTransaction() {
        System.out.println("\n--- [TEARDOWN] HOÀN TIỀN (REVERT DATA) ---");
        int transferAmount = 50;

        System.out.println("-> Đang hoàn lại $" + transferAmount + " từ TK " + accountId2 + " về lại TK " + accountId1);

        // Chuyển ngược lại từ accountId2 sang accountId1
        Response revertRes = RestAssured.given()
                .accept(ContentType.JSON)
                .post("https://parabank.parasoft.com/parabank/services/bank/transfer?fromAccountId="
                        + accountId2 + "&toAccountId=" + accountId1 + "&amount=" + transferAmount);

        // [CHỐT CHẶN]: Khẳng định API trả về 200 Thành công
        Assert.assertEquals(revertRes.statusCode(), 200, "LỖI TEARDOWN: Hoàn tiền thất bại!");
        System.out.println("-> Đã hoàn tiền xong! Dữ liệu số dư về nguyên trạng như trước khi Test.");
    }
}