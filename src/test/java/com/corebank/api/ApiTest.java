package com.corebank.api;

import com.corebank.models.UserInfo;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

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
        System.out.println("Địa chỉ chính xác đang ở là: " + user.getAddress().getStreet() + " Thành phố " + user.getAddress().getCity());


        // 3. Assert (Kiểm chứng)
        Assert.assertEquals(user.getId(), 1, "Sai ID rồi!");
        Assert.assertEquals(user.getName(), "Leanne Graham", "Sai tên rồi!");
    }
        @Test
        public void testGetListUsersAPI() {
            // 1. GỌI API LẤY MẢNG VÀ HỨNG VÀO List (Mảng)
            // Mẹo: RestAssured không hỗ trợ ép trực tiếp ra List<UserInfo>.class,
            // ta ép nó ra UserInfo[].class (Mảng tĩnh), rồi dùng Arrays.asList để ép sang List (Mảng động).
            UserInfo[] mangUser = RestAssured
                    .given()
                    .baseUri("https://jsonplaceholder.typicode.com")
                    .when()
                    .get("/users") // <-- Bỏ số 1 đi để lấy 1 list
                    .as(UserInfo[].class);

            List<UserInfo> danhSachUser = Arrays.asList(mangUser);

            // 2. KHOE SỨC MẠNH CỦA LIST
            System.out.println("Tổng số User lấy về được là: " + danhSachUser.size());

            // 3. VÒNG LẶP: Quét qua toàn bộ mảng để tìm đúng ông "Ervin Howell" (ID = 2)
            boolean timThayErvin = false;
            for (UserInfo user : danhSachUser) {
                if (user.getId() == 2) {
                    System.out.println("Đã tìm thấy Ervin! Địa chỉ của ông này là: " + user.getAddress().getCity());
                    Assert.assertEquals(user.getName(), "Ervin Howell", "Sai tên rồi!");
                    timThayErvin = true;
                    break;
                }
            }

            // Kiểm chứng xem có tìm thấy người này trong mảng không
            Assert.assertTrue(timThayErvin, "Lỗi: Không tìm thấy ông Ervin Howell trong danh sách!");
        }
    }
