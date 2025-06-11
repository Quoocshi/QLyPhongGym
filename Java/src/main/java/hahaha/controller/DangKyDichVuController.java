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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hahaha.enums.LoaiDichVu;
import hahaha.model.BoMon;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.DichVu;
import hahaha.model.HoaDon;
import hahaha.model.KhachHang;
import hahaha.model.Lop;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.DichVuRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.service.DichVuService;
import hahaha.service.HoaDonService;
import hahaha.service.LopService;

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
    @Autowired
        LopService lopService;
    

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
                                         @RequestParam(value = "thoiHanFilter", required = false) String thoiHanFilter,
                                         Model model) {
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }

        String maKH = khachHang.getMaKH();
        String username = khachHang.getAccount().getUserName();

        // Lấy dịch vụ theo bộ môn và filter (nếu có)
        List<DichVu> dichVuList;
        if (thoiHanFilter != null && !thoiHanFilter.isEmpty()) {
            dichVuList = dichVuService.getDichVuTheoBoMonVaThoiHanKhachHangChuaDangKy(maBM, maKH, thoiHanFilter);
        } else {
            dichVuList = dichVuService.getDichVuTheoBoMonKhachHangChuaDangKy(maBM, maKH);
        }
        
        BoMon boMon = dichVuService.getBoMonById(maBM);

        System.out.println("DichVu list size: " + dichVuList.size());
        for (DichVu dv : dichVuList) {
            System.out.println("DichVu: " + dv.getMaDV() + " - " + dv.getTenDV() + " - " + dv.getLoaiDV());
        }

        model.addAttribute("dsDichVu", dichVuList);  // Template đang tìm dsDichVu, không phải dichVuList
        model.addAttribute("selectedBoMon", boMon);  // Template đang tìm selectedBoMon, không phải boMon
        model.addAttribute("selectedThoiHanFilter", thoiHanFilter);  // Để hiển thị filter hiện tại
        model.addAttribute("maKH", maKH);
        model.addAttribute("accountId", accountId);
        model.addAttribute("username", username);

        return "User/dangkydv";
    }

    // TODO: Implement thêm dịch vụ vào giỏ hàng
    @PostMapping("/them-gio-hang")
    @PreAuthorize("hasRole('USER')")
    public String themDichVuVaoGioHang(
            @RequestParam("accountId") Long accountId,
            @RequestParam("maDV") String maDV,
            RedirectAttributes redirectAttributes, 
            Authentication authentication) {

        // Lấy username từ authentication
        String username = authentication.getName();
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }

        System.out.println("Username: " + username);
        System.out.println("AccountId: " + accountId);
        System.out.println("MaDV: " + maDV);

        // TODO: Logic thêm vào giỏ hàng thực tế
        
        redirectAttributes.addFlashAttribute("successMessage", "Đã thêm dịch vụ vào giỏ hàng!");
        return "redirect:/dich-vu-gym/dang-kydv?accountId=" + accountId;
    }

    @GetMapping("/thanh-toan")
    @PreAuthorize("hasRole('USER')")
    public String hienThiTrangThanhToan(@RequestParam("accountId") Long accountId, Model model) {
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }
        
        String maKH = khachHang.getMaKH();
        String username = khachHang.getAccount().getUserName();
        
        model.addAttribute("maKH", maKH);
        model.addAttribute("accountId", accountId);
        model.addAttribute("username", username);
        
        return "User/thanhtoan";
    }

    @PostMapping("/thanh-toan")
    @PreAuthorize("hasRole('USER')")
    public String xuLyThanhToan(@RequestParam("accountId") Long accountId,
                               @RequestParam("dichvu") String[] dichVuCodes,
                               @RequestParam("tongtien") Double tongTien,
                               RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== XỬ LÝ THANH TOÁN VNPAY ===");
            System.out.println("AccountId: " + accountId);
            System.out.println("Tổng tiền: " + tongTien);
            System.out.println("Số dịch vụ: " + dichVuCodes.length);
            
            KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
            if (khachHang == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
            }
            
            String maKH = khachHang.getMaKH();
            
            // Tạo hóa đơn
            HoaDon hoaDon = hoaDonService.taoHoaDon(maKH, tongTien);
            String maHD = hoaDon.getMaHD();
            
            System.out.println("Đã tạo hóa đơn: " + maHD);
            
            // Thêm chi tiết dịch vụ vào hóa đơn
            for (String maDV : dichVuCodes) {
                hoaDonService.themChiTietHoaDon(maHD, maDV);
                System.out.println("Đã thêm dịch vụ: " + maDV);
            }
            
            // Redirect đến trang thanh toán VNPay
            return "redirect:/thanh-toan/" + maHD;
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo hóa đơn: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra khi tạo hóa đơn: " + e.getMessage());
            return "redirect:/dich-vu-gym/dang-kydv?accountId=" + accountId;
        }
    }

    @GetMapping("/chonlop")
    @PreAuthorize("hasRole('USER')")
    public String hienThiChonLop(@RequestParam("maDV") String maDV,
                                @RequestParam("accountId") Long accountId,
                                Model model) {
        try {
            KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
            if (khachHang == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
            }
            
            String maKH = khachHang.getMaKH();
            String username = khachHang.getAccount().getUserName();
            
            // Lấy thông tin dịch vụ
            DichVu dichVu = dichVuRepository.findById(maDV).orElse(null);
            if (dichVu == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dịch vụ với mã: " + maDV);
            }
            
            System.out.println("=== DEBUG Controller ===");
            System.out.println("MaDV: " + maDV);
            System.out.println("TenDV: " + dichVu.getTenDV());
            System.out.println("LoaiDV enum: " + dichVu.getLoaiDV());
            System.out.println("LoaiDV toString: '" + dichVu.getLoaiDV().toString() + "'");
            System.out.println("Is enum Lop: " + (dichVu.getLoaiDV() == hahaha.enums.LoaiDichVu.Lop));
            
            // Kiểm tra xem dịch vụ có phải loại "Lop" không (sử dụng enum)
            if (dichVu.getLoaiDV() != LoaiDichVu.Lop) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dịch vụ này không phải loại lớp. LoaiDV hiện tại: '" + dichVu.getLoaiDV() + "'");
            }
            
            // Lấy danh sách lớp chưa đầy theo bộ môn của dịch vụ
            String maBM = dichVu.getBoMon().getMaBM();
            System.out.println("MaBM: " + maBM);
            System.out.println("Thời hạn dịch vụ: " + dichVu.getThoiHan() + " ngày");
            
            List<Lop> dsLopChuaDay = lopService.getLopChuaDayByBoMon(maBM);
            System.out.println("Tổng số lớp chưa đầy: " + (dsLopChuaDay != null ? dsLopChuaDay.size() : "null"));
            
            // Lọc lớp có thời hạn phù hợp với dịch vụ (thời hạn lớp <= thời hạn dịch vụ)
            if (dsLopChuaDay != null && dichVu.getThoiHan() != null) {
                dsLopChuaDay = dsLopChuaDay.stream()
                    .filter(lop -> {
                        int thoiHanLop = lopService.getThoiHanLop(lop);
                        boolean phuHop = thoiHanLop > 0 && thoiHanLop <= dichVu.getThoiHan();
                        System.out.println("Lớp " + lop.getMaLop() + " - Thời hạn: " + thoiHanLop + " ngày - Phù hợp: " + phuHop);
                        return phuHop;
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            System.out.println("Số lớp sau khi lọc: " + (dsLopChuaDay != null ? dsLopChuaDay.size() : "null"));
            
            model.addAttribute("dichVu", dichVu);
            model.addAttribute("dsLopChuaDay", dsLopChuaDay);
            model.addAttribute("maKH", maKH);
            model.addAttribute("accountId", accountId);
            model.addAttribute("username", username);
            model.addAttribute("lopService", lopService);
            
            return "User/chonlop";
            
        } catch (Exception e) {
            System.err.println("=== LỖI trong hienThiChonLop ===");
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "error";
        }
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
        
        System.out.println("=== DEBUG DỊCH VỤ CỦA TÔI ===");
        System.out.println("AccountId: " + accountId);
        System.out.println("MaKH: " + maKH);
        System.out.println("Username: " + username);
        
        // Tạm thời lấy tất cả dịch vụ đã đăng ký để debug
        List<ChiTietDangKyDichVu> dsDichVuTatCa = chiTietDangKyDichVuRepository.findByKhachHang_MaKH(maKH);
        // Lấy danh sách dịch vụ đã đăng ký và đã thanh toán
        List<ChiTietDangKyDichVu> dsDichVuDaDangKy = chiTietDangKyDichVuRepository.findByKhachHang_MaKH_DaThanhToan(maKH);
        
        System.out.println("=== KIỂM TRA DỮ LIỆU ===");
        System.out.println("Tổng số dịch vụ đã đăng ký (tất cả): " + (dsDichVuTatCa != null ? dsDichVuTatCa.size() : "null"));
        if (dsDichVuTatCa != null && !dsDichVuTatCa.isEmpty()) {
            for (ChiTietDangKyDichVu ct : dsDichVuTatCa) {
                System.out.println("  - " + ct.getMaCTDK() + 
                                 " | HĐ: " + (ct.getHoaDon() != null ? ct.getHoaDon().getMaHD() : "null") +
                                 " | Trạng thái: " + (ct.getHoaDon() != null ? ct.getHoaDon().getTrangThai() : "null"));
            }
        }
        
        System.out.println("Số dịch vụ đã đăng ký: " + (dsDichVuDaDangKy != null ? dsDichVuDaDangKy.size() : "null"));
        
        if (dsDichVuDaDangKy != null && !dsDichVuDaDangKy.isEmpty()) {
            for (ChiTietDangKyDichVu ct : dsDichVuDaDangKy) {
                System.out.println("Chi tiết: " + ct.getMaCTDK() + 
                                 " - Dịch vụ: " + (ct.getDichVu() != null ? ct.getDichVu().getTenDV() : "null") +
                                 " - Hóa đơn: " + (ct.getHoaDon() != null ? ct.getHoaDon().getMaHD() : "null") +
                                 " - Trạng thái HĐ: " + (ct.getHoaDon() != null ? ct.getHoaDon().getTrangThai() : "null"));
            }
        } else {
            System.out.println("Không có dịch vụ nào đã đăng ký!");
        }
        
        model.addAttribute("dsDichVuDaDangKy", dsDichVuDaDangKy);
        model.addAttribute("maKH", maKH);
        model.addAttribute("accountId", accountId);
        model.addAttribute("username", username);
        model.addAttribute("khachHang", khachHang); // Thêm biến khachHang
        
        return "User/dvcuatoi";
    }

    @PostMapping("/dang-ky-dv")
    @PreAuthorize("hasRole('USER')")
    public String dangKyDichVu(@RequestParam("maKH") String maKH,
                              @RequestParam("accountId") Long accountId,
                              @RequestParam("dsMaDV") String[] dsMaDV,
                              @RequestParam(value = "dsClassId", required = false) String[] dsClassId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
            if (khachHang == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
            }
            
            System.out.println("=== ĐĂNG KÝ DỊCH VỤ ===");
            System.out.println("MaKH: " + maKH);
            System.out.println("AccountId: " + accountId);
            System.out.println("Số dịch vụ: " + dsMaDV.length);
            
            // Tạo hóa đơn
            double tongTien = 0;
            
            // Tính tổng tiền từ các dịch vụ
            for (int i = 0; i < dsMaDV.length; i++) {
                String maDV = dsMaDV[i];
                DichVu dichVu = dichVuRepository.findById(maDV).orElse(null);
                
                if (dichVu != null) {
                    tongTien += dichVu.getDonGia();
                    System.out.println("Dịch vụ: " + maDV + " - Giá: " + dichVu.getDonGia());
                    
                    // Kiểm tra nếu có classId được gửi kèm
                    if (dsClassId != null && i < dsClassId.length && !dsClassId[i].isEmpty()) {
                        System.out.println("Lớp được chọn: " + dsClassId[i]);
                    }
                }
            }
            
            // Tạo hóa đơn
            HoaDon hoaDon = hoaDonService.taoHoaDon(maKH, tongTien);
            String maHD = hoaDon.getMaHD();
            
            // Thêm chi tiết dịch vụ vào hóa đơn
            for (int i = 0; i < dsMaDV.length; i++) {
                String maDV = dsMaDV[i];
                String classId = (dsClassId != null && i < dsClassId.length) ? dsClassId[i] : null;
                
                // Gọi service để thêm chi tiết với thông tin lớp (nếu có)
                hoaDonService.themChiTietHoaDonVoiLop(maHD, maDV, classId);
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Đăng ký dịch vụ thành công! Mã hóa đơn: " + maHD);
            redirectAttributes.addFlashAttribute("maHD", maHD);
            
            return "redirect:/dich-vu-gym/dang-kydv?accountId=" + accountId;
            
        } catch (Exception e) {
            System.err.println("Lỗi khi đăng ký dịch vụ: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Có lỗi xảy ra khi đăng ký dịch vụ: " + e.getMessage());
            
            return "redirect:/dich-vu-gym/dang-kydv?accountId=" + accountId;
        }
    }
}
