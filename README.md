# 🏦 Core Banking Automation Test Framework

Dự án Automation Testing theo chuẩn Hybrid (UI + API) mô phỏng lại các luồng nghiệp vụ thực tế của Ngân hàng Lõi (Core Banking).

Được thiết kế theo kiến trúc **Page Object Model (POM)** và hướng tới việc tự động hóa các luồng giao dịch tài chính phức tạp (Maker - Checker).

## 🚀 Công nghệ sử dụng (Tech Stack)
- **Ngôn ngữ:** Java 17
- **UI Automation:** Selenium WebDriver (Mô hình POM)
- **API Automation:** REST Assured (Xử lý XML/JSON)
- **Test Framework:** TestNG (Quản lý Data-Driven & Luồng thực thi)
- **CI/CD:** GitHub Actions (Automated Pipelines)
- **Báo cáo (Report):** Allure Report (Host public trên GitHub Pages)

## 💡 Điểm nhấn Kỹ thuật & Nghiệp vụ (Key Highlights)

### 1. Hybrid Automation (Cắt giảm 70% thời gian Test)
- Áp dụng chiến lược Test Hybrid cho luồng **Maker - Checker**:
    - Dùng **API** đóng vai Maker: Bắn request tạo tài khoản và nạp tiền (Setup Data ngầm).
    - Dùng **Selenium (UI)** đóng vai Checker: Đăng nhập và thực hiện thao tác chuyển khoản.
    - Tốc độ thực thi giảm từ ~45s (Thuần UI) xuống còn **~13s**.

### 2. Tư duy Quản trị Dữ liệu Ngân hàng (Data Pollution & Reversal)
- Hiểu rõ nguyên tắc **Bất biến (Immutable)** của dữ liệu Core Bank.
- Tại hàm `@AfterClass` (Teardown), không xóa dữ liệu vật lý mà sử dụng API để tự động thực hiện **Bút toán đảo (Reversal Transaction)**, hoàn trả lại số dư về tài khoản gốc, đảm bảo cân bằng sổ sách (EOD - End Of Day).

### 3. Phân tích Giá trị biên & Data-Driven (Mô phỏng QĐ 2345/NHNN)
- Thiết kế DataProvider quét các khoảng ranh giới nguy hiểm của luồng chuyển tiền:
    - Giao dịch dưới hạn mức tối thiểu (VD: 1,999 VNĐ)
    - Giao dịch an toàn bằng SmartOTP (VD: < 10,000,000 VNĐ)
    - Giao dịch kích hoạt luồng FaceID (VD: >= 10,000,000 VNĐ)
    - Giao dịch vượt hạn mức Customer Profile (Standard vs Premium).

### 4. Tích hợp CI/CD & Auto Reporting
- Tích hợp GitHub Actions tự động trigger khi có Push/Pull Request.
- Tự động Build, chạy TestNG, xuất báo cáo Allure HTML và Publish thẳng lên GitHub Pages.
- **[👉 CLICK VÀO ĐÂY ĐỂ XEM BÁO CÁO ALLURE LIVE]**(https://drzunk.github.io/CoreBankTest/)

## 📂 Kiến trúc Thư mục