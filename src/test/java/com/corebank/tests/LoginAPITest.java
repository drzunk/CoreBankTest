package com.corebank.tests;

import com.corebank.api.endpoints.AuthAPI;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LoginAPITest {

    @DataProvider(name = "loginData")
    public Object[][] getLoginData() {
        return new Object[][]{
                // username, password,  mã_lỗi_kỳ_vọng,  câu_lỗi_kỳ_vọng
                {"john",     "demo",      200, ""},                                       // Ca dương
                {"john",     "sai_pass",  400, "Invalid username and/or password"},       // Ca âm (Sai pass)
                {"user_fake","123456",    400, "Invalid username and/or password"},       // Ca âm (User ảo)
                {"",         "",          404, ""}                                        // Ca âm (Trống -> Mất URL)
        };
    }

    @Test(description = "Kiểm tra Đăng nhập Data-Driven", dataProvider = "loginData")
    public void testLogin(String username, String password, int expectedStatus, String expectedError) {

        System.out.println("Đang test: [" + username + "] / [" + password + "]");

        Response response = AuthAPI.login(username, password);

        // 1. Assert Status Code (Chốt chặn đầu tiên)
        Assert.assertEquals(response.statusCode(), expectedStatus,
                "Lỗi: Server trả về HTTP Status Code không đúng thực tế!");

        // 2. Xử lý logic động dựa trên HTTP Status
        switch (expectedStatus) {
            case 200:
                int customerId = response.jsonPath().getInt("id");
                Assert.assertTrue(customerId > 0, "Lỗi: ID Khách hàng không hợp lệ");
                System.out.println("-> Sucees! Customer ID: " + customerId);
                break;

            case 400:
                String actualError = response.getBody().asString();
                Assert.assertTrue(actualError.contains(expectedError),
                        "Lỗi: Câu thông báo sai! Thực tế là: " + actualError);
                System.out.println("-> Bad request: " + expectedError);
                break;

            case 404:
                // Mã 404 thường trả về HTML của server Tomcat, không có JSON thông báo lỗi
                System.out.println("-> 404 (Not Found) do API URL thiếu tham số.");
                break;

            default:
                Assert.fail("Status code " + expectedStatus + " chưa được định nghĩa logic xử lý!");
        }
        System.out.println("---------------------------------------------------");
    }
}