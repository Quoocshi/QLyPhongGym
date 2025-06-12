package hahaha.controller;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hahaha.model.DoanhThu;
import hahaha.model.TongQuanDoanhThu;
import hahaha.service.ThongKeService;

@RestController
@RequestMapping("/api/thongke") 
public class ThongKeController {
    @Autowired
    ThongKeService thongKeService;

    @GetMapping("/doanhthu")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> thongKeDoanhThu() {
        List<DoanhThu> list = thongKeService.layDoanhThuTheoNgay();

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");

        for (DoanhThu dthu : list) {
            labels.add(dthu.getNgay().toLocalDate().format(fmt));
            data.add(dthu.getTongTien());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tongquan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TongQuanDoanhThu> getTongQuan() {
        Long tongDangKy = thongKeService.layTongLuotDangKy();
        Long tongThanhToan = thongKeService.layTongLuotThanhToan();
        Double doanhThuThang = thongKeService.layTongDoanhThuThang();
        if (doanhThuThang == null) doanhThuThang = 0.0;

        TongQuanDoanhThu result = new TongQuanDoanhThu(tongDangKy, tongThanhToan, doanhThuThang);
        return ResponseEntity.ok(result);
    }


}
