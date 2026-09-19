package com.corebank.api.endpoints;

import com.corebank.api.BaseAPI;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class AuthAPI {

    /**
     * Gửi request Đăng nhập đến ParaBank.
     * Tương đương API: GET /login/{username}/{password}
     */
    public static Response login(String username, String password) {
        return RestAssured.given()
                .spec(BaseAPI.getBaseRequest())
                .pathParam("user", username)
                .pathParam("pass", password)
                .when()
                .get("/login/{user}/{pass}")
                .then()
                .extract().response(); // Trả về nguyên cái Response để bên Test tự xử lý
    }
}