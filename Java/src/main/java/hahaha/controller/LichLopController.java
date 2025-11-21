package hahaha.controller;

import java.util.*;
import java.util.stream.Collectors;

import hahaha.DTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import hahaha.model.*;
import hahaha.repository.*;
import hahaha.service.LichTapService;
import hahaha.service.LopService;

@RestController
@RequestMapping("/api/trainer")
public class LichLopController {

    @Autowired private LopService lopService;
    @Autowired private LichTapService lichTapService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private CaTapRepository caTapRepository;
    @Autowired private KhuVucRepository khuVucRepository;
    @Autowired private ChiTietDangKyDichVuRepository chiTietDangKyDichVuRepository;

    // --- 1. Lấy danh sách lớp của trainer ---
    @GetMapping("/lich-lop")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<?> getLichLop(Authentication authentication) {
        try {
            String username = authentication.getName();
            Account account = accountRepository.findAccountByUserName(username);
            if (account == null || account.getNhanVien() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy thông tin huấn luyện viên"));
            }

            NhanVien trainer = account.getNhanVien();
            List<Lop> dsLop = lopService.getLopsByTrainerMaNV(trainer.getMaNV());

            // --- map danh sách lớp sang DTO ---
            List<LopDTO> dsLopDto = dsLop.stream().map(l -> {
                LopDTO dto = new LopDTO();
                dto.setMaLop(l.getMaLop());
                dto.setTenLop(l.getTenLop());
                dto.setMoTa(l.getMoTa());
                dto.setSlToiDa(l.getSlToiDa());
                dto.setTinhTrangLop(l.getTinhTrangLop() != null ? l.getTinhTrangLop().name() : "");
                dto.setNgayBD(l.getNgayBD().toLocalDate());
                dto.setNgayKT(l.getNgayKT().toLocalDate());
                dto.setGhiChu(l.getGhiChu());

                // chỉ cần tên bộ môn, không trả toàn bộ danh sách dịch vụ
                BoMonDTO boMonDTO = new BoMonDTO();
                boMonDTO.setMaBM(l.getBoMon().getMaBM());
                boMonDTO.setTenBM(l.getBoMon().getTenBM());
                dto.setBoMon(boMonDTO);

                return dto;
            }).toList();

            // --- map trainer sang DTO ---
            ChiTietNhanVienDTO trainerDTO = new ChiTietNhanVienDTO();
            trainerDTO.setMaNV(trainer.getMaNV());
            trainerDTO.setTenNV(trainer.getTenNV());
            trainerDTO.setEmail(trainer.getEmail());
            trainerDTO.setNgaySinh(trainer.getNgaySinh());
            trainerDTO.setGioiTinh(trainer.getGioiTinh());
            trainerDTO.setNgayVaoLam(trainer.getNgayVaoLam());
            trainerDTO.setLoaiNV(trainer.getLoaiNV().toString());

            return ResponseEntity.ok(Map.of(
                    "trainer", trainerDTO,
                    "dsLop", dsLopDto
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    // --- 2. Lấy lịch PT của trainer ---
    @GetMapping("/lich-canhan")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<?> getLichPT(Authentication authentication) {
        try {
            String username = authentication.getName();
            Account account = accountRepository.findAccountByUserName(username);
            if (account == null || account.getNhanVien() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Không tìm thấy thông tin huấn luyện viên"));
            }

            NhanVien trainer = account.getNhanVien();
            String maNV = trainer.getMaNV();

            List<ChiTietDangKyDichVu> dsPTCustomers = lichTapService.getPTCustomersByTrainer(maNV);
            List<LichTap> dsPTSchedules = lichTapService.getPTScheduleByTrainer(maNV);
            List<CaTap> dsCaTap = caTapRepository.findAll();
            List<KhuVuc> dsKhuVuc = khuVucRepository.findAll();

            return ResponseEntity.ok(Map.of(
                    "trainer", trainer,
                    "maNV", maNV,
                    "dsPTCustomers", dsPTCustomers,
                    "dsPTSchedules", dsPTSchedules,
                    "dsCaTap", dsCaTap,
                    "dsKhuVuc", dsKhuVuc
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // --- 3. Tạo lịch PT ---
    @PostMapping("/tao-lich-pt")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<?> taoLichPT(
            Authentication authentication,
            @RequestBody TaoLichPTRequest request) {
        try {
            String username = authentication.getName();
            Account account = accountRepository.findAccountByUserName(username);
            if (account == null || account.getNhanVien() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Không tìm thấy thông tin huấn luyện viên"));
            }

            String maNV = account.getNhanVien().getMaNV();
            LichTap lichTap = lichTapService.createPTScheduleWithDate(
                    maNV,
                    request.getMaKH(),
                    request.getNgayTap(),
                    request.getCaTap(),
                    request.getMaKV()
            );

            if (lichTap != null) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Tạo lịch PT thành công!",
                        "maLT", lichTap.getMaLT()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Không thể tạo lịch PT. Kiểm tra lại thông tin."));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }


    // --- 4. Kiểm tra xung đột ---
    @PostMapping("/kiem-tra-xung-dot")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<?> kiemTraXungDot(Authentication authentication,
                                            @RequestBody KiemTraXungDotRequest request) {
        try {
            String username = authentication.getName();
            Account account = accountRepository.findAccountByUserName(username);
            if (account == null || account.getNhanVien() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Không tìm thấy thông tin huấn luyện viên"));
            }

            String maNV = account.getNhanVien().getMaNV();
            boolean hasConflict = lichTapService.hasScheduleConflict(maNV, request.getNgay(), request.getCaTap());

            return ResponseEntity.ok(Map.of("hasConflict", hasConflict));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // --- 5. Debug PT customers ---
    @GetMapping("/debug/ptCustomers")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<?> debugPTCustomers(Authentication authentication) {
        try {
            String username = authentication.getName();
            Account account = accountRepository.findAccountByUserName(username);
            if (account == null || account.getNhanVien() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Không tìm thấy thông tin huấn luyện viên"));
            }
            String maNV = account.getNhanVien().getMaNV();
            List<ChiTietDangKyDichVu> ptCustomers = chiTietDangKyDichVuRepository.findPTCustomersByTrainer(maNV);

            List<Map<String, Object>> customerData = ptCustomers.stream().map(pt -> {
                Map<String, Object> data = new HashMap<>();
                data.put("maCTDK", pt.getMaCTDK());
                data.put("maKH", pt.getHoaDon().getKhachHang().getMaKH());
                data.put("tenKH", pt.getHoaDon().getKhachHang().getHoTen());
                data.put("ngayBD", pt.getNgayBD());
                data.put("ngayKT", pt.getNgayKT());
                data.put("tenDV", pt.getDichVu().getTenDV());
                data.put("trangThaiHD", pt.getHoaDon().getTrangThai());
                return data;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("success", true, "data", customerData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
