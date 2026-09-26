package com.corebank.api;

import com.corebank.models.UserInfo;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApiTest {

    @Test
    public void testGetUserAPI() {
        // 1. Gọi API thật và ép nó chảy vào Khuôn UserInfo
        UserInfo user = RestAssured
                .given()
                .baseUri("https://jsonplaceholder.typicode.com")
                .when()
                .get("/users/1")
                .as(UserInfo.class); // Tà thuật biến JSON thành Object

        // 2. In ra màn hình xem đã hút được dữ liệu chưa
        System.out.println("Tên của User tải về là: " + user.getName());
        System.out.println("Email của User là: " + user.getEmail());
        System.out.println("Thành phố đang ở là: " + user.getAddress().getCity());


        // 3. Assert (Kiểm chứng)
        Assert.assertEquals(user.getId(), 1, "Sai ID rồi!");
        Assert.assertEquals(user.getName(), "Leanne Graham", "Sai tên rồi!");
    }
}