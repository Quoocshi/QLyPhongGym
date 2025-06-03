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

import hahaha.model.BoMon;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.DichVu;
import hahaha.model.HoaDon;
import hahaha.model.KhachHang;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.DichVuRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.service.DichVuService;
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
    @Autowired
        DichVuService dichVuService;
    @Autowired
        ChiTietDangKyDichVuRepository chiTietDangKyDichVuRepository;
    

    @GetMapping("/dang-kydv")
    @PreAuthorize("hasRole('USER')")
    public String hienThiDanhSachBoMon(@RequestParam("accountId") Long accountId, Model model) {
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        System.out.println("Account ID nhận vào: " + accountId);
        if (khachHang == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }
        String maKH = khachHang.getMaKH();
        String username = khachHang.getAccount().getUserName();
        System.out.println("MaKH la: " + maKH);

        // Lấy danh sách bộ môn
        List<BoMon> dsBoMon = dichVuService.getAllBoMon();
        model.addAttribute("dsBoMon", dsBoMon);
        model.addAttribute("maKH", maKH); 
        model.addAttribute("accountId", accountId);
        model.addAttribute("username", username);
        return "User/chon-bo-mon";
    }
    
    @GetMapping("/dich-vu-theo-bo-mon")
    @PreAuthorize("hasRole('USER')")
    public String hienThiDichVuTheoBoMon(@RequestParam("maBM") String maBM,
                                        @RequestParam("accountId") Long accountId,
                                        Model model) {
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }
        
        String maKH = khachHang.getMaKH();
        String username = khachHang.getAccount().getUserName();
        
        // Lấy thông tin bộ môn
        List<BoMon> allBoMon = dichVuService.getAllBoMon();
        BoMon selectedBoMon = allBoMon.stream()
            .filter(bm -> bm.getMaBM().equals(maBM))
            .findFirst()
            .orElse(null);
            
        if (selectedBoMon == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bộ môn");
        }

        // Lấy dịch vụ theo bộ môn mà khách hàng chưa đăng ký
        List<DichVu> dsDichVu = dichVuService.getDichVuTheoBoMonKhachHangChuaDangKy(maBM, maKH);
        
        model.addAttribute("selectedBoMon", selectedBoMon);
        model.addAttribute("dsDichVu", dsDichVu);
        model.addAttribute("maKH", maKH); 
        model.addAttribute("accountId", accountId);
        model.addAttribute("username", username);
        return "User/dangkydv";
    }

    
    // @PostMapping("/dang-ky-dv")
    // @PreAuthorize("hasRole('USER')")
    // public String xuLyDangKy(@RequestParam("maKH") String maKH,
    //                             @RequestParam("dsMaDV") List<String> dsMaDV) {
    //     KhachHang kh = khachHangRepository.findById(maKH).orElseThrow();
    //     HoaDon hoaDon = hoaDonService.createHoaDon(kh, dsMaDV);
    //     return "redirect:/thanh-toan/" + hoaDon.getMaHD();
    // }

    @PostMapping("/dang-ky-dv")
@PreAuthorize("hasRole('USER')")
public String xuLyDangKy(@RequestParam("maKH") String maKH,
                         @RequestParam("dsMaDV") List<String> dsMaDV) {
    KhachHang kh = khachHangRepository.findById(maKH).orElseThrow();
    String dsMaDVString = String.join(",", dsMaDV);

    // Gọi service và nhận mã hóa đơn mới
    String maHD = hoaDonService.createHoaDon(kh, dsMaDVString);

    // Redirect đến trang thanh toán
    return "redirect:/thanh-toan/" + maHD;
}



    @GetMapping("/dich-vu-cua-toi")
    @PreAuthorize("hasRole('USER')")
    public String hienThiDichVuCuaToi(@RequestParam("accountId") Long accountId, Model model) {
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }
        
        String maKH = khachHang.getMaKH();
        String username = khachHang.getAccount().getUserName();
        
        // Lấy danh sách dịch vụ đã đăng ký của khách hàng
        List<ChiTietDangKyDichVu> dsDichVuDaDangKy = chiTietDangKyDichVuRepository.findByHoaDon_KhachHang_MaKHOrderByNgayBDDesc(maKH);
        
        model.addAttribute("dsDichVuDaDangKy", dsDichVuDaDangKy);
        model.addAttribute("maKH", maKH);
        model.addAttribute("accountId", accountId);
        model.addAttribute("username", username);
        model.addAttribute("khachHang", khachHang);
        
        return "User/dvcuatoi";
    }
}

