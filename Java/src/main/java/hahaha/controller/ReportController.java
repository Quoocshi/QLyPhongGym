package hahaha.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import hahaha.enums.TrangThaiHoaDon;
import hahaha.model.HoaDon;
import hahaha.repository.HoaDonRepository;
import hahaha.service.ReportService;

@RestController
public class ReportController {
    @Autowired
    HoaDonRepository hoaDonRepository;

    @Autowired
    ReportService reportService;

    @GetMapping("/report/hoadon/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InputStreamResource> exportExcelHoaDon() {
        try {
            List<HoaDon> dsHoaDon = hoaDonRepository.findByTrangThai(TrangThaiHoaDon.DaThanhToan);

            ByteArrayInputStream in = reportService.exportPaidInvoicesToExcel(dsHoaDon);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename = bao_cao_doanh_thu.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(in));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
