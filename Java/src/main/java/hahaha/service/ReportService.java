package hahaha.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import hahaha.model.HoaDon;

@Service
public class ReportService {

    public ByteArrayInputStream exportPaidInvoicesToExcel(List<HoaDon> hoaDons) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Hóa Đơn Đã Thanh Toán");

            // Tạo style cho tiêu đề
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            String[] headers = {"Mã HĐ", "Tên KH", "SĐT", "Ngày lập", "Ngày TT", "Tổng tiền"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (HoaDon hd : hoaDons) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(hd.getMaHD());
                row.createCell(1).setCellValue(hd.getKhachHang().getHoTen());
                row.createCell(2).setCellValue(hd.getKhachHang().getSoDienThoai());
                row.createCell(3).setCellValue(hd.getNgayLap().toString());
                row.createCell(4).setCellValue(hd.getNgayTT() != null ? hd.getNgayTT().toString() : "");
                row.createCell(5).setCellValue(hd.getTongTien());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}

