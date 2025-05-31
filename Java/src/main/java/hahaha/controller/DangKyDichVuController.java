package hahaha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import hahaha.model.DichVu;
import hahaha.model.HoaDon;
import hahaha.model.KhachHang;
import hahaha.repository.DichVuRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.service.HoaDonService;

@Controller
@RequestMapping("/dich-vu-gym")
public class DangKyDichVuController{
    @Autowired
        DichVuRepository dichVuRepository;
    @Autowired
        KhachHangRepository khachHangRepository;
    @Autowired
        HoaDonService hoaDonService;
    

    @GetMapping("/dang-kydv")
    @PreAuthorize("hasRole('USER')")
    public String hienThiDichVuDangKy(@RequestParam("accountId") Long accountId, Model model) {
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        System.out.println("Account ID nhận vào: " + accountId);
        if (khachHang == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }
        String maKH = khachHang.getMaKH();
        String username = khachHang.getAccount().getUserName();
        System.out.println("MaKH la: " + maKH);

        //Hiển thị các dịch vụ khách hàng chưa đăng ký
        List<DichVu> dichVuChuaDangKy = dichVuRepository.listDichVuKhachHangChuaDangKy(khachHang.getMaKH());
        model.addAttribute("dsDichVu", dichVuChuaDangKy);
        model.addAttribute("maKH", maKH); 
        model.addAttribute("accountId", accountId);
        model.addAttribute("username", username);
        return "User/dangkydv";
    }

    
    @PostMapping("/dang-ky-dv")
    @PreAuthorize("hasRole('USER')")
    public String xuLyDangKy(@RequestParam("maKH") String maKH,
                                @RequestParam("dsMaDV") List<String> dsMaDV) {
        KhachHang kh = khachHangRepository.findById(maKH).orElseThrow();
        HoaDon hoaDon = hoaDonService.createHoaDon(kh, dsMaDV);
        return "redirect:/thanh-toan/" + hoaDon.getMaHD();
        }
}

