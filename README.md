# 🏦 CoreBank API Automation Testing

Dự án Automation Test giả lập hệ thống Core Banking (dựa trên ParaBank API), được thiết kế theo kiến trúc chuẩn của một dự án thực tế.

## 🛠️ Công nghệ sử dụng (Tech Stack)
* **Ngôn ngữ:** Java 17
* **Framework:** REST Assured, TestNG
* **Build Tool:** Maven
* **CI/CD:** GitHub Actions (Đang triển khai)

## 🏗️ Kiến trúc Framework (API Object Model)
Dự án áp dụng mô hình phân lớp rõ ràng để dễ bảo trì và mở rộng:
* `com.corebank.api.BaseAPI`: Cấu hình gốc (Base URL, Headers mặc định).
* `com.corebank.api.endpoints`: Chứa định nghĩa các API (Login, Create Account, Transfer...).
* `com.corebank.tests`: Chứa kịch bản Test (Test Scripts).

## 🚀 Các kỹ thuật Nâng cao đã áp dụng (Advanced Techniques)

### 1. Data-Driven Testing
Sử dụng `@DataProvider` của TestNG để kiểm thử chức năng Đăng nhập với nhiều luồng dữ liệu (Valid, Invalid, Missing fields) chỉ bằng một hàm Test duy nhất.
* **Xử lý linh hoạt HTTP Status Code:** Không "hardcode" trạng thái, dùng `switch-case` để phân luồng (400 bắt lỗi JSON, 404 bắt lỗi mất Path Parameter).

### 2. Test Data Strategy (Chiến lược dữ liệu)
Không phụ thuộc vào dữ liệu có sẵn (tránh Flaky test). Kịch bản chuyển tiền (`ParabankRealTest`) được áp dụng vòng đời khép kín:
* **[Tiền trạm - @BeforeClass]:** Đăng nhập -> Lấy thông tin tài khoản gốc -> Tự động gọi API mở thêm 1 tài khoản Tiết kiệm để nhận tiền.
* **[Thực thi - @Test]:** Tiến hành giao dịch chuyển khoản $50 (Transfer Funds).
* **[Dọn dẹp - @AfterMethod]:** Hoàn tiền (Revert Data) - Chuyển ngược $50 về lại tài khoản gốc, trả số dư về nguyên trạng.

## ⚙️ Hướng dẫn cài đặt và chạy (How to run)
1. Clone dự án về máy: `git clone https://github.com/drzunk/CoreBankTest.git`
2. Tải thư viện Maven: `mvn clean install`
3. Chạy toàn bộ Test: `mvn test`