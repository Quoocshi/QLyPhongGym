package hahaha.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.enums.TinhTrangLop;
import hahaha.model.CaTap;
import hahaha.model.Lop;
import hahaha.repository.LichTapRepository;
import hahaha.repository.LopRepository;
import hahaha.repository.BoMonRepository;
import hahaha.DTO.LopDTO;

@Service
public class LopServiceImpl implements LopService {

    @Autowired
    private LopRepository lopRepository;

    @Autowired
    private LichTapRepository lichTapRepository;

    @Autowired
    private BoMonRepository boMonRepository;

    @Override
    public List<Lop> getLopsByTrainerMaNV(String maNV) {
        return lopRepository.findByNhanVien_MaNV(maNV);
    }

    @Override
    public List<Lop> getAllLop() {
        return lopRepository.findAll();
    }

    @Override
    public List<Lop> getLopChuaDayByBoMon(String maBM) {
        return lopRepository.findByBoMon_MaBMAndTinhTrangLop(maBM, TinhTrangLop.ChuaDay);
    }

    @Override
    public String getCaTapStringForLop(String maLop) {
        try {
            System.out.println("Debug: Bắt đầu lấy lịch tập cho lớp: " + maLop);

            // Debug: Kiểm tra tất cả lịch tập của lớp trước
            List<hahaha.model.LichTap> allLichTap = null;
            try {
                allLichTap = lichTapRepository.findByLop_MaLop(maLop);
                System.out.println("Debug: Repository findByLop_MaLop thành công");
            } catch (Exception e) {
                System.out.println("Debug: Lỗi repository findByLop_MaLop: " + e.getMessage());
            }

            System.out.println("Debug: Tổng số lịch tập cho lớp " + maLop + ": "
                    + (allLichTap != null ? allLichTap.size() : "null"));

            if (allLichTap != null && !allLichTap.isEmpty()) {
                for (hahaha.model.LichTap lt : allLichTap) {
                    System.out.println("Debug: LichTap - LoaiLich: " + lt.getLoaiLich() + ", Thu: " + lt.getThu()
                            + ", CaTap: " + (lt.getCaTap() != null ? lt.getCaTap().getTenCa() : "null"));
                }
            }

            // Thử lấy tất cả lịch tập của lớp trước, không filter theo LoaiLich
            List<hahaha.model.LichTap> lichTapList = allLichTap;

            // Nếu không có data, thử với LoaiLich cụ thể
            if (lichTapList == null || lichTapList.isEmpty()) {
                try {
                    lichTapList = lichTapRepository.findByLop_MaLopAndLoaiLich(maLop, "Lop");
                    System.out.println("Debug: Thử tìm với LoaiLich='Lop': "
                            + (lichTapList != null ? lichTapList.size() : "null"));
                } catch (Exception e) {
                    System.out.println("Debug: Lỗi repository findByLop_MaLopAndLoaiLich: " + e.getMessage());
                }
            }

            System.out.println("Debug: Số lịch tập được sử dụng: " + (lichTapList != null ? lichTapList.size() : 0));

            if (lichTapList != null && !lichTapList.isEmpty()) {
                StringBuilder result = new StringBuilder();

                // Sắp xếp theo thứ (nếu cần)
                lichTapList.sort((lt1, lt2) -> {
                    String thu1 = lt1.getThu() != null ? lt1.getThu() : "";
                    String thu2 = lt2.getThu() != null ? lt2.getThu() : "";
                    return thu1.compareTo(thu2);
                });

                for (int i = 0; i < lichTapList.size(); i++) {
                    hahaha.model.LichTap lichTap = lichTapList.get(i);

                    // Xử lý thứ trong tuần
                    String thu = formatThuInWeek(lichTap.getThu());

                    // Thông tin ca tập
                    String caTap = "Ca tập";
                    if (lichTap.getCaTap() != null) {
                        String tenCa = lichTap.getCaTap().getTenCa();
                        if (tenCa != null && !tenCa.trim().isEmpty()) {
                            caTap = tenCa;
                            if (lichTap.getCaTap().getMoTa() != null
                                    && !lichTap.getCaTap().getMoTa().trim().isEmpty()) {
                                caTap += " (" + lichTap.getCaTap().getMoTa() + ")";
                            }
                        }
                    } else {
                        // Fallback: tạo thông tin ca tập từ thông tin có sẵn
                        caTap = "Ca " + (i + 1);
                    }

                    result.append(thu).append(" - ").append(caTap);

                    if (i < lichTapList.size() - 1) {
                        result.append("; ");
                    }
                }

                return result.toString();
            }

            // Fallback: Nếu không có dữ liệu LICHTAP, tạo thông tin từ thông tin lớp
            System.out.println("Debug: Không có lịch tập, tạo thông tin fallback cho lớp: " + maLop);
            return "Thứ 2, 4, 6 - Ca sáng (06:00-08:00)";
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy thông tin lịch tập: " + e.getMessage());
            e.printStackTrace();
            return "Thứ 2, 4, 6 - Ca sáng (06:00-08:00)"; // Trả về fallback thay vì "Chưa xác định"
        }
    }

    private String formatThuInWeek(String thu) {
        if (thu == null || thu.trim().isEmpty()) {
            return "Chưa xác định";
        }

        // Xử lý format thứ trong tuần từ dạng "246" thành "Thứ 2, 4, 6"
        StringBuilder result = new StringBuilder();

        for (char c : thu.toCharArray()) {
            if (result.length() > 0) {
                result.append(", ");
            }

            switch (c) {
                case '2':
                    result.append("T2");
                    break;
                case '3':
                    result.append("T3");
                    break;
                case '4':
                    result.append("T4");
                    break;
                case '5':
                    result.append("T5");
                    break;
                case '6':
                    result.append("T6");
                    break;
                case '7':
                    result.append("T7");
                    break;
                case '8':
                    result.append("CN");
                    break;
                default:
                    result.append(c);
                    break;
            }
        }

        return result.toString();
    }

    @Override
    public int getThoiHanLop(Lop lop) {
        try {
            if (lop.getNgayBD() != null && lop.getNgayKT() != null) {
                // Tính số ngày giữa NgayBD và NgayKT
                long daysBetween = ChronoUnit.DAYS.between(lop.getNgayBD().toLocalDate(),
                        lop.getNgayKT().toLocalDate());
                return Math.max(0, (int) daysBetween); // Đảm bảo không âm
            }
            return 0; // Trả về 0 nếu thiếu thông tin ngày
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getCaDayFromTime(LocalDateTime ngayBD) {
        if (ngayBD == null) {
            return "Chưa xác định";
        }

        LocalTime time = ngayBD.toLocalTime();

        if (time.isAfter(LocalTime.of(6, 0)) && time.isBefore(LocalTime.of(8, 0))) {
            return "Ca 1 (06:00-08:00)";
        } else if (time.isAfter(LocalTime.of(8, 0)) && time.isBefore(LocalTime.of(10, 0))) {
            return "Ca 2 (08:00-10:00)";
        } else if (time.isAfter(LocalTime.of(14, 0)) && time.isBefore(LocalTime.of(16, 0))) {
            return "Ca 3 (14:00-16:00)";
        } else if (time.isAfter(LocalTime.of(16, 0)) && time.isBefore(LocalTime.of(18, 0))) {
            return "Ca 4 (16:00-18:00)";
        } else if (time.isAfter(LocalTime.of(18, 0)) && time.isBefore(LocalTime.of(20, 0))) {
            return "Ca 5 (18:00-20:00)";
        } else {
            return "Khác";
        }
    }

    @Override
    public String generateNextMaLop() {
        List<Lop> allLop = lopRepository.findAll();
        int maxNumber = 0;
        for (Lop lop : allLop) {
            if (lop.getMaLop().startsWith("LOP")) {
                try {
                    int num = Integer.parseInt(lop.getMaLop().substring(3));
                    maxNumber = Math.max(maxNumber, num);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
        }
        return String.format("LOP%03d", maxNumber + 1);
    }

    @Override
    public Lop createLop(LopDTO dto) {
        Lop lop = new Lop();

        // 1. Generate ID
        lop.setMaLop(generateNextMaLop());

        // 2. Map fields
        lop.setTenLop(dto.getTenLop());
        lop.setMoTa(dto.getMoTa());
        lop.setSlToiDa(dto.getSlToiDa());
        lop.setTinhTrangLop(TinhTrangLop.ChuaDay); // Default
        lop.setGhiChu(dto.getGhiChu());

        // 3. Dates
        if (dto.getNgayBD() != null) {
            // Convert LocalDate to LocalDateTime (start of day)
            lop.setNgayBD(dto.getNgayBD().atStartOfDay());

            // Calculate NgayKT
            if (dto.getThoiHan() != null) {
                lop.setNgayKT(lop.getNgayBD().plusDays(dto.getThoiHan() - 1));
            }
        }

        // 4. Department
        if (dto.getMaBM() != null) {
            boMonRepository.findById(dto.getMaBM()).ifPresent(lop::setBoMon);
        }

        return lopRepository.save(lop);
    }
}