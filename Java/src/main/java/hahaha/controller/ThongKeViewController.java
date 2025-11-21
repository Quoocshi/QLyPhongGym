//package hahaha.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import hahaha.enums.TrangThaiHoaDon;
//import hahaha.model.HoaDon;
//import hahaha.repository.HoaDonRepository;
//
//@Controller
//public class ThongKeViewController {
//    @Autowired
//    private HoaDonRepository hoaDonRepository;
//
//    @GetMapping("/thongke/doanhthu")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String hienThiBieuDo(Model model) {
//        List<HoaDon> dsHoaDon =  hoaDonRepository.findByTrangThai(TrangThaiHoaDon.DaThanhToan);
//
//        model.addAttribute("dsHoaDon",dsHoaDon);
//        return "Admin/doanhthu";
//    }
//}
