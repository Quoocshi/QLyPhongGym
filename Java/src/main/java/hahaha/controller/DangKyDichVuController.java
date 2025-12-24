package hahaha.controller;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;
import java.util.Map;

import hahaha.DTO.*;
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
@RequestMapping("/api/dich-vu-gym")
public class DangKyDichVuController {

    @Autowired
    private DichVuRepository dichVuRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private DichVuService dichVuService;
    @Autowired
    private ChiTietDangKyDichVuRepository chiTietDangKyDichVuRepository;
    @Autowired
    private LopService lopService;
    @Autowired
    private NhanVienService nhanVienService;
    @Autowired
    private LichTapRepository lichTapRepository;

    @Autowired
    private javax.sql.DataSource dataSource;
    @Autowired
    private AccountRepository accountRepository;

    // ✅ 1. Lấy danh sách bộ môn
    @GetMapping("/dang-kydv")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiDanhSachBoMon(Authentication authentication) {
        String username = authentication.getName();
        Account acc = accountRepository.findAccountByUserName(username);
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(acc.getAccountId());
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy khách hàng"));

        List<BoMon> dsBoMon = dichVuService.getAllBoMon();

        List<BoMonDTO> boMonDTOs = dsBoMon.stream().map(b -> {
            BoMonDTO dto = new BoMonDTO();
            dto.setMaBM(b.getMaBM());
            dto.setTenBM(b.getTenBM());
            dto.setDanhSachDichVu(
                    b.getDanhSachDichVu().stream().map(d -> {
                        DichVuDTO ddto = new DichVuDTO();
                        ddto.setMaDV(d.getMaDV());
                        ddto.setTenDV(d.getTenDV());
                        ddto.setLoaiDV(d.getLoaiDV().name());
                        ddto.setThoiHan(d.getThoiHan());
                        ddto.setDonGia(d.getDonGia());
                        return ddto;
                    }).toList());
            return dto;
        }).toList();

        ChiTietKhachHangDTO khDTO = new ChiTietKhachHangDTO();
        khDTO.setMaKH(khachHang.getMaKH());
        khDTO.setHoTen(khachHang.getHoTen());
        khDTO.setEmail(khachHang.getEmail());

        return ResponseEntity.ok(Map.of(
                "khachHang", khDTO,
                "dsBoMon", boMonDTOs));
    }

    // ✅ 2. Lấy danh sách dịch vụ theo bộ môn
    @GetMapping("/dich-vu-theo-bo-mon")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiDichVuTheoBoMon(
            @RequestParam("maBM") String maBM,
            @RequestParam(value = "thoiHanFilter", required = false) String thoiHanFilter,
            Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(user.getAccountId());
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));

        List<DichVu> dichVuList = (thoiHanFilter != null && !thoiHanFilter.isEmpty())
                ? dichVuService.getDichVuTheoBoMonVaThoiHanKhachHangChuaDangKy(maBM, khachHang.getMaKH(), thoiHanFilter)
                : dichVuService.getDichVuTheoBoMonKhachHangChuaDangKy(maBM, khachHang.getMaKH());

        List<DichVuDTO> dichVuDTOs = dichVuList.stream().map(d -> {
            DichVuDTO dto = new DichVuDTO();
            dto.setMaDV(d.getMaDV());
            dto.setTenDV(d.getTenDV());
            dto.setLoaiDV(d.getLoaiDV().name());
            dto.setThoiHan(d.getThoiHan());
            dto.setDonGia(d.getDonGia());
            return dto;
        }).toList();

        BoMon boMon = dichVuService.getBoMonById(maBM);
        BoMonDTO boMonDTO = new BoMonDTO();
        boMonDTO.setMaBM(boMon.getMaBM());
        boMonDTO.setTenBM(boMon.getTenBM());
        boMonDTO.setDanhSachDichVu(dichVuDTOs);

        return ResponseEntity.ok(Map.of(
                "boMon", boMonDTO));
    }

    // // ✅ 3. Thêm dịch vụ vào giỏ hàng (mock)
    // @PostMapping("/them-gio-hang")
    // @PreAuthorize("hasRole('USER')")
    // public ResponseEntity<?> themDichVuVaoGioHang(
    // @RequestParam("accountId") Long accountId,
    // @RequestParam("maDV") String maDV,
    // Authentication authentication) {
    //
    // KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
    // if (khachHang == null)
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",
    // "Không tìm thấy khách hàng"));
    //
    // return ResponseEntity.ok(Map.of(
    // "message", "Đã thêm dịch vụ vào giỏ hàng!",
    // "maDV", maDV,
    // "username", authentication.getName()
    // ));
    // }

    // // ✅ 4. Xử lý thanh toán
    // @PostMapping("/tao-hoa-don")
    // @PreAuthorize("hasRole('USER')")
    // public ResponseEntity<?> xuLyThanhToan(
    // @RequestParam("accountId") Long accountId,
    // @RequestParam("dichvu") String[] dichVuCodes,
    // @RequestParam("tongtien") Double tongTien) {
    // try {
    // KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
    // if (khachHang == null)
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",
    // "Không tìm thấy khách hàng"));
    //
    // HoaDon hoaDon = hoaDonService.taoHoaDon(khachHang.getMaKH(), tongTien);
    // for (String maDV : dichVuCodes)
    // hoaDonService.themChiTietHoaDon(hoaDon.getMaHD(), maDV);
    //
    // return ResponseEntity.ok(Map.of(
    // "message", "Tạo hóa đơn thành công",
    // "maHD", hoaDon.getMaHD(),
    // "tongTien", tongTien
    // ));
    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    // .body(Map.of("error", e.getMessage()));
    // }
    // }

    // ✅ 5. Chọn lớp cho dịch vụ loại "Lớp"
    @GetMapping("/chonlop")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiChonLop(@RequestParam("maDV") String maDV,
            Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(user.getAccountId());
        DichVu dichVu = dichVuRepository.findById(maDV).orElse(null);
        if (dichVu == null || dichVu.getLoaiDV() != LoaiDichVu.Lop)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Dịch vụ không hợp lệ"));

        List<Lop> dsLopChuaDay = lopService.getLopChuaDayByBoMon(dichVu.getBoMon().getMaBM());
        if (dichVu.getThoiHan() != null)
            dsLopChuaDay = dsLopChuaDay.stream()
                    .filter(l -> lopService.getThoiHanLop(l) <= dichVu.getThoiHan())
                    .toList();

        List<LopDTO> lopDTOs = dsLopChuaDay.stream().map(l -> {
            LopDTO dto = new LopDTO();
            dto.setMaLop(l.getMaLop());
            dto.setTenLop(l.getTenLop());
            dto.setMoTa(l.getMoTa());
            dto.setSlToiDa(l.getSlToiDa());
            dto.setTinhTrangLop(l.getTinhTrangLop().name());
            dto.setGhiChu(l.getGhiChu());
            dto.setNgayBD(l.getNgayBD().toLocalDate());
            dto.setNgayKT(l.getNgayKT().toLocalDate());

            // 1. Populate Instructor Name
            if (l.getNhanVien() != null) {
                dto.setTenGV(l.getNhanVien().getTenNV());
            }

            // 2. Fetch Schedule and Room from LichTap
            List<LichTap> lichTaps = lichTapRepository.findByLop_MaLop(l.getMaLop());
            if (lichTaps != null && !lichTaps.isEmpty()) {
                // Get Room from the first schedule entry (assuming class always in same room)
                if (lichTaps.get(0).getKhuVuc() != null) {
                    dto.setPhong(lichTaps.get(0).getKhuVuc().getTenKhuVuc());
                }

                // Format Schedule string: "Mon-Wed-Fri (18:00-19:30)"
                // Simplified logic: Append distinct Thu + Ca
                StringBuilder lichHocStr = new StringBuilder();
                for (LichTap lt : lichTaps) {
                    if (lt.getThu() != null) {
                        lichHocStr.append(lt.getThu()).append(" ");
                    }
                    if (lt.getCaTap() != null) {
                        lichHocStr.append("(").append(lt.getCaTap().getTenCa()).append(") ");
                    }
                }
                // Clean up string
                String finalLich = lichHocStr.toString().trim().replace("  ", " ");
                // Simple dedup logic if needed, but for now just raw concatenation is better
                // than nothing
                dto.setLichHoc(finalLich);
            }

            return dto;
        }).toList();

        DichVuDTO dichVuDTO = new DichVuDTO();
        dichVuDTO.setMaDV(dichVu.getMaDV());
        dichVuDTO.setTenDV(dichVu.getTenDV());
        dichVuDTO.setLoaiDV(dichVu.getLoaiDV().name());
        dichVuDTO.setThoiHan(dichVu.getThoiHan());
        dichVuDTO.setDonGia(dichVu.getDonGia());

        return ResponseEntity.ok(Map.of(
                "dichVu", dichVuDTO,
                "dsLopChuaDay", lopDTOs));
    }

    // ✅ 6. Chọn PT cho dịch vụ loại "PT"
    @GetMapping("/chonpt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiChonPT(
            @RequestParam("maDV") String maDV,
            Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        Long accountId = user.getAccountId();
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
        List<NhanVienDTO> dsTrainerDTO = dsTrainer.stream()
                .map(NhanVienDTO::new)
                .toList();
        return ResponseEntity.ok(Map.of(
                "dichVu", dichVu,
                "dsTrainer", dsTrainerDTO));
    }

    // ✅ 7. Danh sách dịch vụ đã đăng ký
    @GetMapping("/dich-vu-cua-toi")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> hienThiDichVuCuaToi(Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        Long accountId = user.getAccountId();
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(accountId);
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));

        ChiTietKhachHangDTO khDTO = new ChiTietKhachHangDTO();
        khDTO.setMaKH(khachHang.getMaKH());
        khDTO.setHoTen(khachHang.getHoTen());
        khDTO.setEmail(khachHang.getEmail());

        List<ChiTietDangKyDichVu> ds = chiTietDangKyDichVuRepository
                .findByKhachHang_MaKH_DaThanhToan(khachHang.getMaKH());

        List<ChiTietDichVuDTO> dsDTO = ds.stream().map(ct -> {
            ChiTietDichVuDTO dto = new ChiTietDichVuDTO();
            dto.setMaDV(ct.getDichVu().getMaDV());
            dto.setTenDichVu(ct.getDichVu().getTenDV());
            // Date Logic
            if (ct.getLop() != null) {
                // Class: Use Class dates
                dto.setNgayBD(ct.getLop().getNgayBD());
                dto.setNgayKT(ct.getLop().getNgayKT());
            } else {
                // Service / PT: Use Registration Date + Duration
                if (ct.getHoaDon() != null && ct.getHoaDon().getNgayLap() != null) {
                    dto.setNgayBD(ct.getHoaDon().getNgayLap());
                    if (ct.getDichVu().getThoiHan() != null) {
                        dto.setNgayKT(ct.getHoaDon().getNgayLap().plusDays(ct.getDichVu().getThoiHan() - 1));
                    } else {
                        dto.setNgayKT(ct.getNgayKT()); // Fallback
                    }
                } else {
                    dto.setNgayBD(ct.getNgayBD());
                    dto.setNgayKT(ct.getNgayKT());
                }
            }
            dto.setGiaTien(ct.getDichVu().getDonGia());

            // Populate Instructor/PT info
            if (ct.getNhanVien() != null) {
                // Personal Trainer case
                dto.setMaNV(ct.getNhanVien().getMaNV());
                dto.setTenNV(ct.getNhanVien().getTenNV());
            } else if (ct.getLop() != null) {
                // Class case
                dto.setTenLop(ct.getLop().getTenLop());
                if (ct.getLop().getNhanVien() != null) {
                    dto.setMaNV(ct.getLop().getNhanVien().getMaNV());
                    dto.setTenNV(ct.getLop().getNhanVien().getTenNV());
                }
            }
            return dto;
        }).toList();

        return ResponseEntity.ok(Map.of(
                "khachHang", khDTO,
                "dichVuDaDangKy", dsDTO));
    }

    // 8. Gọi procedure để đăng ký dịch vụ (TuDo/PT/Lớp)
    @PostMapping("/dang-ky-dv-universal")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> dangKyDichVuUniversal(@RequestBody DangKyDichVuRequest request) {
        KhachHang khachHang = khachHangRepository.findByAccount_AccountId(request.getAccountId());
        if (khachHang == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy khách hàng"));

        String[] result = callUniversalProcedure(
                request.getMaKH(),
                request.getDsMaDV().toArray(new String[0]),
                request.getDsTrainerId() != null ? request.getDsTrainerId().toArray(new String[0]) : null,
                request.getDsClassId() != null ? request.getDsClassId().toArray(new String[0]) : null);
        if ("SUCCESS".equals(result[0])) {
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng ký thành công",
                    "maHD", result[1],
                    "tongTien", result[2]));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", result[3]));
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

            return new String[] {
                    result,
                    maHD,
                    tongTien != null ? tongTien.toString() : "0",
                    errorMsg
            };
        } catch (Exception e) {
            return new String[] { "ERROR", null, "0", e.getMessage() };
        }
    }
}
