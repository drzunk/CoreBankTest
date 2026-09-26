package com.corebank.utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;

public class ExcelUtils {

    // Hàm này sẽ trả về mảng 2 chiều Object[][] (Chuẩn xác định dạng mà DataProvider của TestNG cần)
    public static Object[][] getTestData(String excelFilePath, String sheetName) {
        Object[][] data = null;
        try {
            // 1. Mở file như một dòng chảy (Stream)
            FileInputStream file = new FileInputStream(excelFilePath);

            // 2. Mở cuốn sổ làm việc (Workbook - đại diện cho cả file Excel)
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            // 3. Chọn trang giấy cụ thể (Sheet)
            XSSFSheet sheet = workbook.getSheet(sheetName);

            // 4. Lấy số lượng hàng (Row) và số lượng cột (Column) có chứa dữ liệu
            int rowCount = sheet.getPhysicalNumberOfRows(); // Số hàng
            int colCount = sheet.getRow(0).getLastCellNum(); // Số cột ở dòng đầu tiên

            // Tạo một mảng 2 chiều để chứa data (Bỏ qua dòng đầu tiên vì nó là Tiêu đề)
            data = new Object[rowCount - 1][colCount];
            DataFormatter formatter = new DataFormatter(); // Dùng để ép mọi định dạng Excel thành chuỗi String

            // 5. Dùng 2 vòng lặp lồng nhau để quét qua từng Ô (Cell)
            for (int i = 1; i < rowCount; i++) { // Chạy từ i=1 để bỏ qua dòng tiêu đề (Header)
                for (int j = 0; j < colCount; j++) {
                    // Đọc giá trị tại Ô(Hàng i, Cột j) và nhét vào mảng
                    data[i - 1][j] = formatter.formatCellValue(sheet.getRow(i).getCell(j));
                }
            }
            workbook.close();
            file.close();
        } catch (Exception e) {
            System.out.println("Lỗi đọc file Excel: " + e.getMessage());
        }
        return data;
    }
}