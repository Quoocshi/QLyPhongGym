package hahaha.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import hahaha.model.BoMon;
import hahaha.model.DichVu;
import hahaha.repository.BoMonRepository;
import hahaha.repository.DichVuRepository;

@Service
public class DichVuServiceImpl implements DichVuService {
    
    @Autowired
    private DichVuRepository dichVuRepository;
    
    @Autowired
    private BoMonRepository boMonRepository;

    @Override
    public List<DichVu> getAll() {
        return dichVuRepository.findAll();
    }

    @Override
    public DichVu findById(String maDV) {
        return dichVuRepository.findById(maDV).orElse(null);
    }

    @Override
    public Boolean createDichVu(DichVu dichVu) {
        try {
            // Kiểm tra mã dịch vụ đã tồn tại chưa
            if (dichVuRepository.existsById(dichVu.getMaDV())) {
                System.err.println("Mã dịch vụ đã tồn tại: " + dichVu.getMaDV());
                return false;
            }
            
            dichVuRepository.save(dichVu);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo dịch vụ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Boolean updateDichVu(DichVu dichVu) {
        try {
            jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
                try (CallableStatement cs = connection.prepareCall(
                        "CALL procedure_update_dichvu(?, ?, ?, ?, ?, ?, ?)")) {

                    cs.setString(1, dichVu.getMaDV());
                    cs.setString(2, dichVu.getTenDV());
                    cs.setString(3, dichVu.getLoaiDV().toString());

                    if (dichVu.getThoiHan() != null) {
                        cs.setInt(4, dichVu.getThoiHan());
                    } else {
                        cs.setNull(4, java.sql.Types.INTEGER);
                    }

                    if (dichVu.getDonGia() != null) {
                        cs.setDouble(5, dichVu.getDonGia());
                    } else {
                        cs.setNull(5, java.sql.Types.NUMERIC);
                    }

                    cs.setString(6, dichVu.getBoMon().getMaBM());
                    cs.setInt(7, dichVu.getVersion());

                    cs.execute();
                }
                return null;
            });
            return true;
        } catch (DataAccessException dae) {
            Throwable rootCause = dae.getRootCause();
            if (rootCause instanceof SQLException sqlEx) {
                String msg = sqlEx.getMessage();
                if (msg != null && msg.contains("Xung đột phiên bản dịch vụ")) {
                    throw new RuntimeException(msg);
                } else {
                    throw new RuntimeException(msg, dae);
                }
            } else {
                throw new RuntimeException(dae);
            }
        }
    }



    @Autowired
    private DataSource dataSource;
    public Boolean deleteDichVu(String maDV) {
        try (Connection conn = dataSource.getConnection()){
            Thread.sleep(15000);
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            // Kiểm tra dịch vụ có tồn tại không
            if (!dichVuRepository.existsById(maDV)) {
                System.err.println("Dịch vụ không tồn tại: " + maDV);
                return false;
            }
            
            dichVuRepository.deleteById(maDV);
            return true;
        } catch (DataIntegrityViolationException e) {
            System.err.println("Không thể xóa dịch vụ vì có ràng buộc khóa ngoại: " + maDV);
            throw new RuntimeException("Không thể xóa dịch vụ này vì đã có khách hàng đăng ký!", e);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa dịch vụ: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Có lỗi xảy ra khi xóa dịch vụ: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateNextMaDV() {
        List<DichVu> allDichVu = dichVuRepository.findAll();
        int maxNumber = 0;
        
        for (DichVu dv : allDichVu) {
            String maDV = dv.getMaDV();
            if (maDV != null && maDV.startsWith("DV")) {
                try {
                    int number = Integer.parseInt(maDV.substring(2));
                    if (number > maxNumber) {
                        maxNumber = number;
                    }
                } catch (NumberFormatException e) {
                    // Ignore non-numeric suffixes
                }
            }
        }
        
        return String.format("DV%03d", maxNumber + 1);
    }

    @Override
    public List<BoMon> getAllBoMon() {
        return boMonRepository.findAll();
    }
    @Override
    public List<DichVu> getDichVuTheoBoMonKhachHangChuaDangKy(String maBM, String maKH) {
        return dichVuRepository.listDichVuTheoBoMonKhachHangChuaDangKy(maBM, maKH);
    }

    @Override
    public List<DichVu> getDichVuTheoBoMonVaThoiHanKhachHangChuaDangKy(String maBM, String maKH, String thoiHanFilter) {
        return dichVuRepository.listDichVuTheoBoMonVaThoiHanKhachHangChuaDangKy(maBM, maKH, thoiHanFilter);
    }
//
//    @Override
//    public List<DichVu> getTatCaDichVuTheoBoMon(String maBM) {
//        return dichVuRepository.listTatCaDichVuTheoBoMon(maBM);
//    }
//
//    @Override
//    public List<DichVu> getTatCaDichVuTheoBoMonVaThoiHan(String maBM, String thoiHanFilter) {
//        return dichVuRepository.listTatCaDichVuTheoBoMonVaThoiHan(maBM, thoiHanFilter);
//    }
//
//    @Override
//    public List<DichVu> getDichVuByBoMon(String maBM) {
//        return dichVuRepository.findByBoMon_MaBM(maBM);
//    }

    @Override
    public BoMon getBoMonById(String maBM) {
        return boMonRepository.findById(maBM).orElse(null);
    }

} 