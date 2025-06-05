package hahaha.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import hahaha.enums.TrangThaiHoaDon;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.DichVu;
import hahaha.model.HoaDon;
import hahaha.model.KhachHang;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.DichVuRepository;
import hahaha.repository.HoaDonRepository;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    @Autowired private HoaDonRepository hoaDonRepository;
    @Autowired private DichVuRepository dichVuRepository;
    @Autowired private ChiTietDangKyDichVuRepository chiTietRepository;
    @Autowired private ChiTietDangKyDichVuService chiTietService;
    @Autowired private DataSource dataSource;

    // @Override
    // public HoaDon createHoaDon(KhachHang khachHang, List<String> dsMaDV) {

    //     HoaDon hoaDon = new HoaDon();
    //     hoaDon.setMaHD(generateNextMaHD());
    //     hoaDon.setKhachHang(khachHang);
    //     hoaDon.setTrangThai(TrangThaiHoaDon.ChuaThanhToan);
    //     hoaDon.setNgayLap(java.time.LocalDateTime.now());

    //     List<ChiTietDangKyDichVu> dsChiTiet = new ArrayList<>();
    //     double tongGia = 0;

    //     Integer base = chiTietRepository.findMaxChiTietDangKyDichVuNumber();
    //     base = (base != null) ? base + 1 : 1;
        
    //     for (String maDV : dsMaDV) {
    //         try {
    //             DichVu dv = dichVuRepository.findById(maDV).orElse(null);
    //             if (dv != null) {
    //                 ChiTietDangKyDichVu ct = chiTietService.taoChiTiet(dv, hoaDon, base++);
    //                 dsChiTiet.add(ct);
    //                 tongGia += dv.getDonGia();
    //             } else {
    //                 // Tạo dịch vụ mẫu nếu không tìm thấy trong database
    //                 DichVu dvMau = new DichVu();
    //                 dvMau.setMaDV(maDV);
    //                 dvMau.setTenDV(getMockServiceName(maDV));
    //                 dvMau.setDonGia(6999999.0);
    //                 dvMau.setThoiHan(180); // 6 tháng
                    
    //                 ChiTietDangKyDichVu ct = new ChiTietDangKyDichVu();
    //                 ct.setMaCTDK(chiTietService.generateMaCTDKFromNumber(base++));
    //                 ct.setDichVu(dvMau);
    //                 ct.setHoaDon(hoaDon);
    //                 ct.setNgayBD(java.time.LocalDateTime.now());
    //                 ct.setNgayKT(java.time.LocalDateTime.now().plusDays(dvMau.getThoiHan()));
                    
    //                 dsChiTiet.add(ct);
    //                 tongGia += dvMau.getDonGia();
    //             }
    //         } catch (Exception e) {
    //             System.err.println("Error processing service: " + maDV + ", " + e.getMessage());
    //         }
    //     }

    //     hoaDon.setDsChiTiet(dsChiTiet);
    //     hoaDon.setTongTien(tongGia);
    //     try {
    //         Thread.sleep(4000);
    //     } catch (InterruptedException ex) {
    //     }
    //     hoaDonRepository.save(hoaDon); 
    //     return hoaDon;
    // }
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public String createHoaDon(KhachHang khachHang, String dsMaDV) {
        try {
            String maHD = jdbcTemplate.execute((ConnectionCallback<String>) conn -> {
                CallableStatement stmt = conn.prepareCall("{call CreateHoaDonProc(?, ?, ?)}");
                stmt.setString(1, khachHang.getMaKH());
                stmt.setString(2, dsMaDV);
                stmt.registerOutParameter(3, java.sql.Types.VARCHAR);
                stmt.execute();
                return stmt.getString(3); // trả về mã hóa đơn
        });

        System.out.println("Mã hóa đơn tạo ra: " + maHD);
        return maHD;

    } catch (DataAccessException e) {
        Throwable root = e.getCause();
        if (root instanceof SQLException sqlEx) {
            if (sqlEx.getErrorCode() == 20001) {
                throw new RuntimeException("❌ Oracle Error: " + sqlEx.getMessage());
            } else {
                throw new RuntimeException("❌ DB Error: " + sqlEx.getMessage());
            }
        }
        throw e;
    }
    }


    private String getMockServiceName(String maDV) {
        switch (maDV.toUpperCase()) {
            case "GYM": return "GYM - 6 tháng - Tự do";
            case "YOGA": return "Lớp YOGA - 6 tháng - Lớp B1";
            case "ZUMBA": return "Lớp ZUMBA - 6 tháng - Lớp A1";
            case "CARDIO": return "CARDIO - 6 tháng - Tự do";
            case "BƠI": return "BƠI - 6 tháng - Tự do";
            case "GYMPT": return "GYM PT - 6 tháng - Cá nhân";
            default: return maDV + " - 6 tháng";

    @Override
    public HoaDon createHoaDon(KhachHang khachHang, String dsMaDVString) {
        try (Connection connection = dataSource.getConnection()) {
            // Gọi stored procedure
            String sql = "{call proc_dang_ky_dich_vu_tong_hop(?, ?, ?, ?, ?, ?)}";
            
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                // Set input parameters
                stmt.setString(1, khachHang.getMaKH());  // p_ma_kh
                stmt.setString(2, dsMaDVString);         // p_list_ma_dv
                
                // Register output parameters
                stmt.registerOutParameter(3, Types.VARCHAR);  // p_ma_hd
                stmt.registerOutParameter(4, Types.NUMERIC);  // p_tong_tien
                stmt.registerOutParameter(5, Types.VARCHAR);  // p_result
                stmt.registerOutParameter(6, Types.VARCHAR);  // p_error_msg
                
                // Execute procedure
                stmt.execute();
                
                // Get results
                String maHD = stmt.getString(3);
                double tongTien = stmt.getDouble(4);
                String result = stmt.getString(5);
                String errorMsg = stmt.getString(6);
                
                if ("SUCCESS".equals(result)) {
                    // Lấy hóa đơn đã tạo từ database
                    return hoaDonRepository.findById(maHD).orElseThrow(
                        () -> new RuntimeException("Không tìm thấy hóa đơn vừa tạo: " + maHD)
                    );
                } else {
                    throw new RuntimeException("Lỗi đăng ký dịch vụ: " + errorMsg);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kết nối database: " + e.getMessage(), e);

        }
    }

    @Override
    public HoaDon timMaHD(String maHD) {
        return hoaDonRepository.findById(maHD).orElseThrow();
    }

    @Override
    public void thanhToan(String maHD) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "{call proc_thanh_toan_hoa_don(?, ?, ?)}";
            
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                // Set input parameter
                stmt.setString(1, maHD);  // p_ma_hd
                
                // Register output parameters
                stmt.registerOutParameter(2, Types.VARCHAR);  // p_result
                stmt.registerOutParameter(3, Types.VARCHAR);  // p_error_msg
                
                // Execute procedure
                stmt.execute();
                
                // Get results
                String result = stmt.getString(2);
                String errorMsg = stmt.getString(3);
                
                if (!"SUCCESS".equals(result)) {
                    throw new RuntimeException("Lỗi thanh toán: " + errorMsg);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kết nối database: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateNextMaHD(){
        Integer max = hoaDonRepository.findMaxMaHoaDonNumber();
        int next = (max != null) ? max + 1 : 1;
        return String.format("HD%03d", next);
    }

    // Deprecated methods - không còn sử dụng khi đã có procedure
    private String getMockServiceName(String maDV) {
        switch (maDV.toUpperCase()) {
            case "GYM": return "GYM - 6 tháng - Tự do";
            case "YOGA": return "Lớp YOGA - 6 tháng - Lớp B1";
            case "ZUMBA": return "Lớp ZUMBA - 6 tháng - Lớp A1";
            case "CARDIO": return "CARDIO - 6 tháng - Tự do";
            case "BƠI": return "BƠI - 6 tháng - Tự do";
            case "GYMPT": return "GYM PT - 6 tháng - Cá nhân";
            default: return maDV + " - 6 tháng";
        }
    }

}
