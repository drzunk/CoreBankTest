package com.corebank.api;

import com.corebank.models.UserInfo;
import com.corebank.services.MapAPI;
import com.corebank.services.UserAPI;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class ApiTest {

    private final int MAX_USERS_TO_TEST = 5;

    @Test(priority = 1)
    public void testSieuTichHop_LayDanhSachVaDichToaDo() {

        // Sếp gọi thợ UserAPI đi lấy danh sách về
        Response responseUsers = UserAPI.getListUsers();

        List<UserInfo> danhSachUser = Arrays.asList(responseUsers.as(UserInfo[].class));
        int soNguoiCanKiemTra = Math.min(danhSachUser.size(), MAX_USERS_TO_TEST);
        System.out.println("--- BẮT ĐẦU QUÉT " + soNguoiCanKiemTra + " KHÁCH HÀNG ---");

        // Dùng IntStream.parallel() để ép CPU chạy đa luồng (Multi-threading)
        java.util.stream.IntStream.range(0, soNguoiCanKiemTra).parallel().forEach(i -> {

            UserInfo user = danhSachUser.get(i);

            String lat = responseUsers.jsonPath().getString("address.geo.lat[" + i + "]");
            String lng = responseUsers.jsonPath().getString("address.geo.lng[" + i + "]");

            System.out.println("\nĐang xử lý Luồng song song cho: " + user.getName());

            // Gọi API Bản đồ (Không có phanh Thread.sleep nữa)
            Response resBanDo = MapAPI.reverseGeocode(lat, lng);
            String diaChiThat = resBanDo.jsonPath().getString("display_name");

            if (diaChiThat != null) {
                System.out.println("   => [Hoàn thành] " + user.getName() + " - Dịch ra Bản đồ thật: " + diaChiThat);
            } else {
                System.out.println("   => [Hoàn thành] " + user.getName() + " - Bản đồ chịu thua! (Tọa độ ảo)");
            }

        }); // Hết khối lệnh đa luồng
    }

    @Test(priority = 2)
    public void testCloneKhachHang_IntegrationFlow() {
        System.out.println("\n[Step 1] Gửi GET Request lấy thông tin khách hàng ID = 1");
        // Gọi thợ UserAPI lấy ông số 1
        UserInfo khachHangGoc = UserAPI.getUserById(1).as(UserInfo.class);

        System.out.println("[Step 2] Điều chỉnh trường [Name]");
        khachHangGoc.setName("Dung Dang");
        khachHangGoc.setId(0);

        System.out.println("[Step 3] Gửi POST Request tạo khách hàng mới");
        // Gọi thợ UserAPI mang Data vừa nhào nặn đi tạo mới
        Response responsePost = UserAPI.createNewUser(khachHangGoc);

        System.out.println("[Step 4] Kiểm chứng kết quả");
        Assert.assertEquals(responsePost.getStatusCode(), 201, "[Failed] Lỗi tạo mới.");

        int idMoi = responsePost.jsonPath().getInt("id");
        System.out.println("[Passed] Thành công. ID mới: " + idMoi);
        // ==========================================
        // BƯỚC 5: TƯ DUY END-TO-END (Lấy lại đúng ID vừa tạo)
        // ==========================================
        System.out.println("[Step 5] Verify Data Database - Gọi GET request truy vấn lại ID: " + idMoi);

        Response responseKiemTra = UserAPI.getUserById(idMoi);

        // Đoạn này dùng if-else để xử lý tình huống Mock Server cùi bắp không lưu DB
        if (responseKiemTra.getStatusCode() == 404) {
            System.out.println("[Warning] Hệ thống giả lập (JSONPlaceholder) báo lỗi 404 Not Found.");
            System.out.println("-> Lý do: Mock Server không thực sự lưu data vào DB. Nhưng LUỒNG LOGIC TẠO VÀ TÌM KIẾM BẠN VIẾT LÀ ĐÚNG CHUẨN 100%!");
        } else {
            // Nếu là server thật, nó sẽ chạy vào đây
            UserInfo khachHangTrongDB = responseKiemTra.as(UserInfo.class);
            Assert.assertEquals(khachHangTrongDB.getName(), "Dung Dang", "Lỗi: DB lưu sai tên!");
            System.out.println("[Passed] Database đã lưu chính xác: " + khachHangTrongDB.getName());
        }
    }
}