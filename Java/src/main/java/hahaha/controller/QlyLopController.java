package hahaha.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hahaha.DTO.LopDTO;
import hahaha.model.Lop;
import hahaha.service.LopService;

@RestController
@RequestMapping("/api/quan-ly-lop")
public class QlyLopController {

    @Autowired
    private LopService lopService;

    @PostMapping("/them-lop")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF') or hasRole('MANAGER')")
    public ResponseEntity<?> themLop(@RequestBody LopDTO lopDTO) {
        try {
            Lop newLop = lopService.createLop(lopDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Tạo lớp thành công!", "maLop", newLop.getMaLop()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
