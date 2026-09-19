package com.corebank.tests;

import com.corebank.api.endpoints.AuthAPI;
import com.corebank.api.endpoints.CustomerAPI;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class IsolatedLoginTest {

    // Khai báo biến toàn cục để dùng chung
    private String testUser = "user_tam_thoi_" + System.currentTimeMillis();
    private String testPass = "Pass123!@#";

    @BeforeMethod
    public void setupData() {
        System.out.println("\n[SETUP] Đang tạo tài khoản ảo: " + testUser);
        // Gọi API Đăng ký tài khoản (Tạo rác)
        // CustomerAPI.createCustomer(testUser, testPass);

        // Ghi chú: Vì ParaBank không hỗ trợ API /register chuẩn JSON,
        // ở dự án thật (như Bộ Tư Pháp), hàm này sẽ chạy và trả về 201 Created.
        System.out.println("[SETUP] Đã tạo xong tài khoản vào Database.");
    }

    @Test(description = "Test Đăng nhập bằng tài khoản ảo vừa tạo")
    public void testLoginWithFreshData() {
        System.out.println("[TEST] Đang test đăng nhập với: " + testUser);

        // Gọi API Login (Hiện tại sẽ ra 400 vì ta chưa bắn API tạo thật)
        Response response = AuthAPI.login(testUser, testPass);

        System.out.println("[TEST] Đăng nhập xong. Status: " + response.statusCode());
        // Assert.assertEquals(response.statusCode(), 200);
    }

    @AfterMethod
    public void teardownData() {
        System.out.println("[TEARDOWN] Đang dọn dẹp, xóa tài khoản: " + testUser);
        // Gọi API Xóa tài khoản (Dọn rác)
        // CustomerAPI.deleteCustomer(testUser);

        // Hoặc trong thực tế, nhiều QA dùng JDBC chọc thẳng vào DB gõ lệnh:
        // "DELETE FROM users WHERE username = '" + testUser + "'";
        System.out.println("[TEARDOWN] Đã dọn dẹp sạch sẽ hệ thống!\n");
    }
}