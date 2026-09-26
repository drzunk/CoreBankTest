package com.corebank.api;

import com.corebank.models.UserInfo;
import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class ApiTest {

    @Test
    public void testGetListUsersAPI_KiemSoatLinhHoat() {

        UserInfo[] mangUser = RestAssured
                .given()
                .baseUri("https://jsonplaceholder.typicode.com")
                .when()
                .get("/users")
                .as(UserInfo[].class);

        List<UserInfo> danhSachUser = Arrays.asList(mangUser);
        int tongSoNguoi = danhSachUser.size();

        // 1. TÍNH TOÁN GIỚI HẠN VÀ CẢNH BÁO
        int soNguoiCanKiemTra = 5;

        if (tongSoNguoi < 5) {
            // Cảnh báo (Không dùng Assert Fail)
            System.err.println("⚠️ CẢNH BÁO MÀU ĐỎ: API chỉ trả về " + tongSoNguoi + " bản ghi (Ít hơn 5).");
            System.err.println("=> Hệ thống sẽ tự động chuyển sang quét toàn bộ " + tongSoNguoi + " bản ghi hiện có!");

            // Ép số vòng lặp xuống bằng đúng số người thực tế
            soNguoiCanKiemTra = tongSoNguoi;
        } else {
            System.out.println("API trả về " + tongSoNguoi + " bản ghi. Đạt tiêu chuẩn, sẽ lấy 5 người đầu tiên để test.");
        }

        // 2. VÒNG LẶP LINH HOẠT TỐI ĐA 5
        System.out.println("--- BẮT ĐẦU KIỂM TRA " + soNguoiCanKiemTra + " NGƯỜI ---");

        for (int i = 0; i < soNguoiCanKiemTra; i++) {
            UserInfo user = danhSachUser.get(i);

            System.out.println("Đang kiểm tra người thứ " + (i+1) + " - Tên: " + user.getName() + " có địa chỉ là: " + user.getAddress().getStreet()+ "," + user.getAddress().getCity());

            Assert.assertTrue(user.getId() > 0,
                    "Lỗi Dữ Liệu: Người dùng tên [" + user.getName() + "] có ID không hợp lệ: " + user.getId());
        }

        System.out.println("Hoàn tất quét dữ liệu linh hoạt!");
    }
}
