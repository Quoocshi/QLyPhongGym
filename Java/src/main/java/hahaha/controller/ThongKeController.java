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
import org.springframework.web.bind.annotation.RestController;

import hahaha.model.DoanhThu;
import hahaha.service.ThongKeService;

@RestController
public class ThongKeController {
    @Autowired
    ThongKeService thongKeService;

    @GetMapping("/api/thongke/doanhthu")
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

}
