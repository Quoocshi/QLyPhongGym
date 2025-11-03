package hahaha.controller;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import hahaha.enums.LoaiDichVu;
import hahaha.model.*;
import hahaha.repository.*;
import hahaha.service.*;

@RestController
@RequestMapping("api/dich-vu-gym")
public class DangKyDichVuController {

    @Autowired private DichVuRepository dichVuRepository;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private HoaDonService hoaDonService;
    @Autowired private DichVuService dichVuService;
    @Autowired private ChiTietDangKyDichVuRepository chiTietDangKyDichVuRepository;
    @Autowired private LopService lopService;
    @Autowired private NhanVienService nhanVienService;
    @Autowired private javax.sql.DataSource dataSource;
    @Autowired private AccountRepository accountRepository;
    // ✅ 1. Lấy danh sách bộ môn
    @GetMapping("/dang-kydv")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiDanhSachBoMon(Authentication authentication) {
        // Lấy username từ JWT token
        String username = authentication.getName();

        // Tìm khách hàng theo username
        Account acc = accountRepository.findAccountByUserName(username);
        Long accountId = acc.getAccountId();
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy khách hàng"));

        List<BoMon> dsBoMon = dichVuService.getAllBoMon();

        return ResponseEntity.ok(Map.of(
                "khachHang", khachHang,
                "dsBoMon", dsBoMon
        ));
    }


    // ✅ 2. Lấy danh sách dịch vụ theo bộ môn
    @GetMapping("/dich-vu-theo-bo-mon")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiDichVuTheoBoMon(
            @RequestParam("maBM") String maBM,
            @RequestParam("accountId") Long accountId,
            @RequestParam(value = "thoiHanFilter", required = false) String thoiHanFilter) {

        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));

        List<DichVu> dichVuList = (thoiHanFilter != null && !thoiHanFilter.isEmpty())
                ? dichVuService.getDichVuTheoBoMonVaThoiHanKhachHangChuaDangKy(maBM, khachHang.getMaKH(), thoiHanFilter)
                : dichVuService.getDichVuTheoBoMonKhachHangChuaDangKy(maBM, khachHang.getMaKH());

        return ResponseEntity.ok(Map.of(
                "boMon", dichVuService.getBoMonById(maBM),
                "dichVuList", dichVuList
        ));
    }

//    // ✅ 3. Thêm dịch vụ vào giỏ hàng (mock)
//    @PostMapping("/them-gio-hang")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> themDichVuVaoGioHang(
//            @RequestParam("accountId") Long accountId,
//            @RequestParam("maDV") String maDV,
//            Authentication authentication) {
//
//        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
//        if (khachHang == null)
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));
//
//        return ResponseEntity.ok(Map.of(
//                "message", "Đã thêm dịch vụ vào giỏ hàng!",
//                "maDV", maDV,
//                "username", authentication.getName()
//        ));
//    }

//    // ✅ 4. Xử lý thanh toán
//    @PostMapping("/tao-hoa-don")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<?> xuLyThanhToan(
//            @RequestParam("accountId") Long accountId,
//            @RequestParam("dichvu") String[] dichVuCodes,
//            @RequestParam("tongtien") Double tongTien) {
//        try {
//            KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
//            if (khachHang == null)
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));
//
//            HoaDon hoaDon = hoaDonService.taoHoaDon(khachHang.getMaKH(), tongTien);
//            for (String maDV : dichVuCodes)
//                hoaDonService.themChiTietHoaDon(hoaDon.getMaHD(), maDV);
//
//            return ResponseEntity.ok(Map.of(
//                    "message", "Tạo hóa đơn thành công",
//                    "maHD", hoaDon.getMaHD(),
//                    "tongTien", tongTien
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }

    // ✅ 5. Chọn lớp cho dịch vụ loại "Lớp"
    @GetMapping("/chonlop")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiChonLop(
            @RequestParam("maDV") String maDV,
            @RequestParam("accountId") Long accountId) {

        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));

        DichVu dichVu = dichVuRepository.findById(maDV).orElse(null);
        if (dichVu == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy dịch vụ"));

        if (dichVu.getLoaiDV() != LoaiDichVu.Lop)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Dịch vụ này không phải loại lớp"));

        List<Lop> dsLopChuaDay = lopService.getLopChuaDayByBoMon(dichVu.getBoMon().getMaBM());
        if (dichVu.getThoiHan() != null) {
            dsLopChuaDay = dsLopChuaDay.stream()
                    .filter(l -> lopService.getThoiHanLop(l) <= dichVu.getThoiHan())
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(Map.of(
                "dichVu", dichVu,
                "dsLopChuaDay", dsLopChuaDay
        ));
    }

    // ✅ 6. Chọn PT cho dịch vụ loại "PT"
    @GetMapping("/chonpt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiChonPT(
            @RequestParam("maDV") String maDV,
            @RequestParam("accountId") Long accountId) {

        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));

        DichVu dichVu = dichVuRepository.findById(maDV).orElse(null);
        if (dichVu == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy dịch vụ"));

        if (dichVu.getLoaiDV() != LoaiDichVu.PT)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Dịch vụ này không phải loại PT"));

        List<NhanVien> dsTrainer = nhanVienService.getTrainersByBoMon(dichVu.getBoMon().getMaBM());
        return ResponseEntity.ok(Map.of(
                "dichVu", dichVu,
                "dsTrainer", dsTrainer
        ));
    }

    // ✅ 7. Danh sách dịch vụ đã đăng ký
    @GetMapping("/dich-vu-cua-toi")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiDichVuCuaToi(@RequestParam("accountId") Long accountId) {
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));

        List<ChiTietDangKyDichVu> ds = chiTietDangKyDichVuRepository.findByKhachHang_MaKH_DaThanhToan(khachHang.getMaKH());
        return ResponseEntity.ok(Map.of(
                "khachHang", khachHang,
                "dichVuDaDangKy", ds
        ));
    }

    // ✅ 8. Gọi procedure universal để đăng ký dịch vụ (TuDo/PT/Lớp)
    @PostMapping("/dang-ky-dv-universal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> dangKyDichVuUniversal(
            @RequestParam("maKH") String maKH,
            @RequestParam("accountId") Long accountId,
            @RequestParam("dsMaDV") String[] dsMaDV,
            @RequestParam(value = "dsTrainerId", required = false) String[] dsTrainerId,
            @RequestParam(value = "dsClassId", required = false) String[] dsClassId) {

        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));

        String[] result = callUniversalProcedure(maKH, dsMaDV, dsTrainerId, dsClassId);
        if ("SUCCESS".equals(result[0])) {
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng ký thành công",
                    "maHD", result[1],
                    "tongTien", result[2]
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", result[3]
            ));
        }
    }

    // --- Private helper: gọi procedure ---
    private String[] callUniversalProcedure(String maKH, String[] dsMaDV,
                                            String[] dsTrainerId, String[] dsClassId) {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall(
                     "{call proc_dang_ky_dich_vu_universal(?, ?, ?, ?, ?, ?, ?, ?)}")) {

            String listMaDV = String.join(",", dsMaDV);
            String listTrainer = dsTrainerId != null ? String.join(",", dsTrainerId) : "";
            String listClass = dsClassId != null ? String.join(",", dsClassId) : "";

            statement.setString(1, maKH);
            statement.setString(2, listMaDV);
            statement.setString(3, listTrainer.isEmpty() ? null : listTrainer);
            statement.setString(4, listClass.isEmpty() ? null : listClass);
            statement.registerOutParameter(5, Types.VARCHAR);
            statement.registerOutParameter(6, Types.NUMERIC);
            statement.registerOutParameter(7, Types.VARCHAR);
            statement.registerOutParameter(8, Types.VARCHAR);

            statement.execute();

            String maHD = statement.getString(5);
            BigDecimal tongTien = statement.getBigDecimal(6);
            String result = statement.getString(7);
            String errorMsg = statement.getString(8);

            return new String[]{
                    result,
                    maHD,
                    tongTien != null ? tongTien.toString() : "0",
                    errorMsg
            };
        } catch (Exception e) {
            return new String[]{"ERROR", null, "0", e.getMessage()};
        }
    }
}
