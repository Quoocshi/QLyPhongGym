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

    @Override
    public HoaDon taoHoaDon(String maKH, Double tongTien) {
        try (Connection connection = dataSource.getConnection()) {
            // Sử dụng procedure để tạo hóa đơn cơ bản
            String sql = "{call proc_dang_ky_dich_vu_tong_hop(?, ?, ?, ?, ?, ?)}";
            
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                // Set input parameters - empty service list để chỉ tạo hóa đơn
                stmt.setString(1, maKH);        // p_ma_kh
                stmt.setString(2, "");          // p_list_ma_dv (empty)
                
                // Register output parameters
                stmt.registerOutParameter(3, Types.VARCHAR);  // p_ma_hd
                stmt.registerOutParameter(4, Types.NUMERIC);  // p_tong_tien
                stmt.registerOutParameter(5, Types.VARCHAR);  // p_result
                stmt.registerOutParameter(6, Types.VARCHAR);  // p_error_msg
                
                // Execute procedure
                stmt.execute();
                
                // Get results
                String maHD = stmt.getString(3);
                String result = stmt.getString(5);
                String errorMsg = stmt.getString(6);
                
                if ("SUCCESS".equals(result)) {
                    // Cập nhật tổng tiền nếu được cung cấp
                    if (tongTien != null && tongTien > 0) {
                        String updateSql = "UPDATE HOADON SET TongTien = ? WHERE MaHD = ?";
                        try (var updateStmt = connection.prepareStatement(updateSql)) {
                            updateStmt.setDouble(1, tongTien);
                            updateStmt.setString(2, maHD);
                            updateStmt.executeUpdate();
                        }
                    }
                    
                    // Lấy hóa đơn đã tạo từ database
                    return hoaDonRepository.findById(maHD).orElseThrow(
                        () -> new RuntimeException("Không tìm thấy hóa đơn vừa tạo: " + maHD)
                    );
                } else {
                    throw new RuntimeException("Lỗi tạo hóa đơn: " + errorMsg);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kết nối database: " + e.getMessage(), e);
        }
    }

    @Override
    public void themChiTietHoaDon(String maHD, String maDV) {
        try {
            // Lấy thông tin dịch vụ
            DichVu dichVu = dichVuRepository.findById(maDV).orElseThrow(
                () -> new RuntimeException("Không tìm thấy dịch vụ: " + maDV)
            );
            
            // Lấy hóa đơn
            HoaDon hoaDon = hoaDonRepository.findById(maHD).orElseThrow(
                () -> new RuntimeException("Không tìm thấy hóa đơn: " + maHD)
            );
            
            // Lấy số thứ tự tiếp theo
            Integer maxNumber = chiTietRepository.findMaxChiTietDangKyDichVuNumber();
            int stt = (maxNumber != null) ? maxNumber + 1 : 1;
            
            // Tạo chi tiết đăng ký dịch vụ
            ChiTietDangKyDichVu chiTiet = chiTietService.taoChiTiet(dichVu, hoaDon, stt);
            
            if (chiTiet == null) {
                throw new RuntimeException("Không thể tạo chi tiết đăng ký cho dịch vụ: " + maDV);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi thêm chi tiết hóa đơn: " + e.getMessage(), e);
        }
    }

    @Override
    public void themChiTietHoaDonVoiLop(String maHD, String maDV, String maLop) {
        try {
            // Lấy thông tin dịch vụ
            DichVu dichVu = dichVuRepository.findById(maDV).orElseThrow(
                () -> new RuntimeException("Không tìm thấy dịch vụ: " + maDV)
            );
            
            // Lấy hóa đơn
            HoaDon hoaDon = hoaDonRepository.findById(maHD).orElseThrow(
                () -> new RuntimeException("Không tìm thấy hóa đơn: " + maHD)
            );
            
            // Lấy số thứ tự tiếp theo
            Integer maxNumber = chiTietRepository.findMaxChiTietDangKyDichVuNumber();
            int stt = (maxNumber != null) ? maxNumber + 1 : 1;
            
            // Tạo chi tiết đăng ký dịch vụ với thông tin lớp
            ChiTietDangKyDichVu chiTiet = chiTietService.taoChiTietVoiLop(dichVu, hoaDon, stt, maLop);
            
            if (chiTiet == null) {
                throw new RuntimeException("Không thể tạo chi tiết đăng ký cho dịch vụ: " + maDV + " với lớp: " + maLop);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi thêm chi tiết hóa đơn với lớp: " + e.getMessage(), e);
        }
    }
}
