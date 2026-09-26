package com.corebank.services;

import com.corebank.models.UserInfo;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserAPI {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    // 1. Hàm lấy danh sách toàn bộ Users
    public static Response getListUsers() {
        return RestAssured.given().baseUri(BASE_URL).when().get("/users");
    }

    // 2. Hàm lấy chi tiết 1 User
    public static Response getUserById(int userId) {
        return RestAssured.given().baseUri(BASE_URL).when().get("/users/" + userId);
    }

    // 3. Hàm tạo mới 1 User
    public static Response createNewUser(UserInfo payload) {
        return RestAssured
                .given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .body(payload)
                .when()
                .post("/users");
    }
}