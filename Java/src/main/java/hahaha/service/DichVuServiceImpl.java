package hahaha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Override
    public Boolean updateDichVu(DichVu dichVu) {
        try {
            // Kiểm tra dịch vụ có tồn tại không
            if (!dichVuRepository.existsById(dichVu.getMaDV())) {
                System.err.println("Dịch vụ không tồn tại: " + dichVu.getMaDV());
                return false;
            }
            
            dichVuRepository.save(dichVu);
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi cập nhật dịch vụ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean deleteDichVu(String maDV) {
        try {
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
} 