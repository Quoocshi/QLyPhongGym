package hahaha.controller;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
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
import hahaha.model.NhanVien;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.DichVuRepository;
import hahaha.repository.KhachHangRepository;
import hahaha.service.DichVuService;
import hahaha.service.HoaDonService;
import hahaha.service.LopService;
import hahaha.service.NhanVienService;

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
    @Autowired
        NhanVienService nhanVienService;
    
    @Autowired
    private javax.sql.DataSource dataSource;
    

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
            // Chỉ hiển thị dịch vụ mà khách hàng chưa đăng ký
            dichVuList = dichVuService.getDichVuTheoBoMonVaThoiHanKhachHangChuaDangKy(maBM, maKH, thoiHanFilter);
        } else {
            // Chỉ hiển thị dịch vụ mà khách hàng chưa đăng ký
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

    @GetMapping("/chonpt")
    @PreAuthorize("hasRole('USER')")
    public String hienThiChonPT(@RequestParam("maDV") String maDV,
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
            
            System.out.println("=== DEBUG Controller ChonPT ===");
            System.out.println("MaDV: " + maDV);
            System.out.println("TenDV: " + dichVu.getTenDV());
            System.out.println("LoaiDV enum: " + dichVu.getLoaiDV());
            
            // Kiểm tra xem dịch vụ có phải loại "PT" không
            if (dichVu.getLoaiDV() != LoaiDichVu.PT) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dịch vụ này không phải loại PT. LoaiDV hiện tại: '" + dichVu.getLoaiDV() + "'");
            }
            
            // Lấy danh sách trainer theo bộ môn của dịch vụ
            String maBM = dichVu.getBoMon().getMaBM();
            System.out.println("MaBM: " + maBM);
            
            List<NhanVien> dsTrainer = nhanVienService.getTrainersByBoMon(maBM);
            System.out.println("Tổng số trainer: " + (dsTrainer != null ? dsTrainer.size() : "null"));
            
            if (dsTrainer != null) {
                for (NhanVien trainer : dsTrainer) {
                    System.out.println("Trainer: " + trainer.getMaNV() + " - " + trainer.getTenNV());
                }
            }
            
            model.addAttribute("dichVu", dichVu);
            model.addAttribute("dsTrainer", dsTrainer);
            model.addAttribute("maKH", maKH);
            model.addAttribute("accountId", accountId);
            model.addAttribute("username", username);
            
            return "User/chonpt";
            
        } catch (Exception e) {
            System.err.println("=== LỖI trong hienThiChonPT ===");
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

    @PostMapping("/dang-ky-dv-universal")
    @PreAuthorize("hasRole('USER')")
    public String dangKyDichVuUniversal(@RequestParam("maKH") String maKH,
                                       @RequestParam("accountId") Long accountId,
                                       @RequestParam("dsMaDV") String[] dsMaDV,
                                       @RequestParam(value = "dsTrainerId", required = false) String[] dsTrainerId,
                                       @RequestParam(value = "dsClassId", required = false) String[] dsClassId,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== ĐĂNG KÝ DỊCH VỤ UNIVERSAL (TuDo + PT + Lop) ===" );
            System.out.println("MaKH: " + maKH);
            System.out.println("AccountId: " + accountId);
            System.out.println("Số dịch vụ: " + dsMaDV.length);
            
            // Debug assignments
            if (dsTrainerId != null) {
                System.out.println("Trainer assignments:");
                for (int j = 0; j < dsTrainerId.length; j++) {
                    System.out.println("  [" + j + "] DV: " + (j < dsMaDV.length ? dsMaDV[j] : "N/A") + 
                                     " → Trainer: " + dsTrainerId[j]);
                }
            }
            
            if (dsClassId != null) {
                System.out.println("Class assignments:");
                for (int j = 0; j < dsClassId.length; j++) {
                    System.out.println("  [" + j + "] DV: " + (j < dsMaDV.length ? dsMaDV[j] : "N/A") + 
                                     " → Class: " + dsClassId[j]);
                }
            }
            
            // Xác thực khách hàng
            KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
            if (khachHang == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy thông tin khách hàng");
            }
            
            if (!khachHang.getMaKH().equals(maKH)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Thông tin khách hàng không khớp");
            }
            
            // Gọi procedure universal
            String[] result = callUniversalProcedure(maKH, dsMaDV, dsTrainerId, dsClassId);
            
            if ("SUCCESS".equals(result[0])) {
                String maHD = result[1];
                String tongTien = result[2];
                
                System.out.println("✅ Đăng ký universal thành công - MaHD: " + maHD + ", TongTien: " + tongTien);
                
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Đăng ký dịch vụ thành công! Mã hóa đơn: " + maHD);
                
                // Redirect trực tiếp đến trang thanh toán theo mã hóa đơn
                return "redirect:/thanh-toan/" + maHD;
            } else {
                System.err.println("❌ Đăng ký thất bại: " + result[3]);
                
                // Cải thiện thông báo lỗi cho người dùng
                String errorMsg = result[3];
                if (errorMsg.contains("đã đăng ký dịch vụ")) {
                    errorMsg = "⚠️ Bạn đã đăng ký dịch vụ này rồi. Vui lòng chọn dịch vụ khác hoặc kiểm tra lại danh sách dịch vụ của bạn.";
                } else if (errorMsg.contains("không tìm thấy")) {
                    errorMsg = "❌ Không tìm thấy thông tin dịch vụ. Vui lòng thử lại.";
                } else {
                    errorMsg = "❌ " + errorMsg;
                }
                
                redirectAttributes.addFlashAttribute("errorMessage", errorMsg);
                return "redirect:/dich-vu-gym/dang-kydv?accountId=" + accountId;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi trong dangKyDichVuUniversal: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/dich-vu-gym/dang-kydv?accountId=" + accountId;
        }
    }
    
    /**
     * Gọi procedure proc_dang_ky_dich_vu_universal
     * @return String array [result, maHD, tongTien, errorMsg]
     */
    private String[] callUniversalProcedure(String maKH, String[] dsMaDV, 
                                          String[] dsTrainerId, String[] dsClassId) {
        Connection connection = null;
        CallableStatement statement = null;
        
        try {
            // Chuẩn bị dữ liệu cho procedure
            String listMaDV = String.join(",", dsMaDV);
            
            // Đảm bảo trainer và class arrays có cùng độ dài với service array
            String[] normalizedTrainerIds = new String[dsMaDV.length];
            String[] normalizedClassIds = new String[dsMaDV.length];
            
            for (int i = 0; i < dsMaDV.length; i++) {
                normalizedTrainerIds[i] = (dsTrainerId != null && i < dsTrainerId.length && 
                                         dsTrainerId[i] != null && !dsTrainerId[i].trim().isEmpty()) 
                                        ? dsTrainerId[i].trim() : "";
                                        
                normalizedClassIds[i] = (dsClassId != null && i < dsClassId.length && 
                                       dsClassId[i] != null && !dsClassId[i].trim().isEmpty()) 
                                      ? dsClassId[i].trim() : "";
            }
            
            String listTrainerId = String.join(",", normalizedTrainerIds);
            String listClassId = String.join(",", normalizedClassIds);
            
            System.out.println("📋 Procedure inputs:");
            System.out.println("  MaKH: " + maKH);
            System.out.println("  Services: " + listMaDV);
            System.out.println("  Trainers: " + listTrainerId);
            System.out.println("  Classes: " + listClassId);
            
            connection = dataSource.getConnection();
            statement = connection.prepareCall(
                "{call proc_dang_ky_dich_vu_universal(?, ?, ?, ?, ?, ?, ?, ?)}"
            );
            
            // Input parameters
            statement.setString(1, maKH);
            statement.setString(2, listMaDV);
            statement.setString(3, listTrainerId.isEmpty() ? null : listTrainerId);
            statement.setString(4, listClassId.isEmpty() ? null : listClassId);
            
            // Output parameters
            statement.registerOutParameter(5, Types.VARCHAR); // p_ma_hd
            statement.registerOutParameter(6, Types.NUMERIC); // p_tong_tien
            statement.registerOutParameter(7, Types.VARCHAR); // p_result
            statement.registerOutParameter(8, Types.VARCHAR); // p_error_msg
            
            // Execute procedure
            System.out.println("🔄 Executing procedure proc_dang_ky_dich_vu_universal...");
            statement.execute();
            
            // Lấy kết quả
            String maHD = statement.getString(5);
            BigDecimal tongTien = statement.getBigDecimal(6);
            String result = statement.getString(7);
            String errorMsg = statement.getString(8);
            
            System.out.println("📋 Procedure result: " + result);
            System.out.println("📋 MaHD: " + maHD);
            System.out.println("📋 TongTien: " + tongTien);
            
            if (errorMsg != null) {
                System.out.println("📋 Error: " + errorMsg);
            }
            
            return new String[]{
                result,
                maHD,
                tongTien != null ? tongTien.toString() : "0",
                errorMsg
            };
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi gọi procedure universal: " + e.getMessage());
            e.printStackTrace();
            return new String[]{"ERROR", null, "0", "Lỗi hệ thống: " + e.getMessage()};
        } finally {
            try {
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi đóng connection: " + e.getMessage());
            }
        }
    }
}
