package com.corebank.api.endpoints;

import com.corebank.api.BaseAPI;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

public class CustomerAPI {

    // 1. Dùng POST để ném data lên server tạo User mới
    public static Response createCustomer(String username, String password) {
        // Tạo một cục JSON ảo chứa thông tin đăng ký
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("password", password);
        payload.put("firstName", "Tam");
        payload.put("lastName", "Thoi");

        return RestAssured.given()
                .spec(BaseAPI.getBaseRequest()) // Kế thừa URL gốc
                .body(payload) // Gắn cục JSON vào Body
                .when()
                .post("/customers/register") // (Ghi chú: Đây là đường dẫn ví dụ)
                .then()
                .extract().response();
    }

    // 2. Dùng DELETE để xóa User sau khi test xong
    public static Response deleteCustomer(String username) {
        return RestAssured.given()
                .spec(BaseAPI.getBaseRequest())
                .pathParam("user", username)
                .when()
                .delete("/customers/{user}")
                .then()
                .extract().response();
    }
}