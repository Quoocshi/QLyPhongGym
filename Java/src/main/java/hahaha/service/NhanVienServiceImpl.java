package hahaha.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import hahaha.repository.NhanVienRepository;
import hahaha.model.NhanVien;
import hahaha.enums.LoaiNhanVien;

@Service
public class NhanVienServiceImpl implements NhanVienService {
    @Autowired
    NhanVienRepository nhanVienRepository;

    @Override
    public String generateNextMaNV() {
        List<NhanVien> allNV = nhanVienRepository.findAllNotDeleted();
        int maxNumber = 0;
        for (NhanVien nv : allNV) {
            if (nv.getMaNV().startsWith("NV")) {
                try {
                    int num = Integer.parseInt(nv.getMaNV().substring(2));
                    maxNumber = Math.max(maxNumber, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid format
                }
            }
        }
        return String.format("NV%03d", maxNumber + 1);
    }

    @Override
    public String generateNextMaQL() {
        List<NhanVien> allNV = nhanVienRepository.findAllNotDeleted();
        int maxNumber = 0;
        for (NhanVien nv : allNV) {
            if (nv.getMaNV().startsWith("QL")) {
                try {
                    int num = Integer.parseInt(nv.getMaNV().substring(2));
                    maxNumber = Math.max(maxNumber, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid format
                }
            }
        }
        return String.format("QL%03d", maxNumber + 1);
    }

    @Override
    public String generateNextMaPT() {
        List<NhanVien> allNV = nhanVienRepository.findAllNotDeleted();
        int maxNumber = 0;
        for (NhanVien nv : allNV) {
            if (nv.getMaNV().startsWith("PT")) {
                try {
                    int num = Integer.parseInt(nv.getMaNV().substring(2));
                    maxNumber = Math.max(maxNumber, num);
                } catch (NumberFormatException e) {
                    // Ignore invalid format
                }
            }
        }
        return String.format("PT%03d", maxNumber + 1);
    }

    @Override
    public List<NhanVien> getAll() {
        return nhanVienRepository.findAllActive();
    }

    @Override
    public NhanVien findById(String maNV) {
        Optional<NhanVien> result = nhanVienRepository.findByIdActive(maNV);
        return result.orElse(null);
    }

    @Override
    public Boolean createNhanVien(NhanVien nhanVien) {
        try {
            nhanVienRepository.save(nhanVien);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean updateNhanVien(NhanVien nhanVien) {
        try {
            if (nhanVienRepository.existsById(nhanVien.getMaNV())) {
                nhanVienRepository.save(nhanVien);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean deleteNhanVien(String maNV) {
        try {
            Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByIdActive(maNV);
            if (nhanVienOpt.isPresent()) {
                NhanVien nhanVien = nhanVienOpt.get();
                nhanVien.setIsDeleted(1); // Soft delete
                nhanVienRepository.save(nhanVien);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<NhanVien> searchNhanVien(String keyword, String loaiNV) {
        try {
            // Normalize keyword để xử lý ký tự có dấu
            if (keyword == null) {
                keyword = "";
            } else {
                keyword = keyword.trim();
                // Ensure UTF-8 encoding
                keyword = new String(keyword.getBytes("UTF-8"), "UTF-8");
            }
            
            System.out.println("Normalized keyword: '" + keyword + "'");
            
            LoaiNhanVien loaiNVEnum = null;
            if (loaiNV != null && !loaiNV.trim().isEmpty()) {
                try {
                    loaiNVEnum = LoaiNhanVien.valueOf(loaiNV);
                } catch (IllegalArgumentException e) {
                    System.err.println("Loại nhân viên không hợp lệ: " + loaiNV);
                    loaiNV = null; // Reset to null if invalid
                }
            } else {
                loaiNV = null;
            }
            
            List<NhanVien> result = nhanVienRepository.searchNhanVien(keyword, loaiNV, loaiNVEnum);
            System.out.println("Query executed successfully, found " + result.size() + " employees");
            
            // Fallback: nếu không tìm được với UTF-8, thử với unaccented search
            if (!keyword.isEmpty() && (result == null || result.size() == 0)) {
                System.out.println("=== TRYING UNACCENTED SEARCH ===");
                List<NhanVien> unaccentedResult = nhanVienRepository.searchNhanVienUnaccented(keyword, loaiNV);
                System.out.println("Unaccented search found: " + unaccentedResult.size() + " employees");
                if (unaccentedResult.size() > 0) {
                    result = unaccentedResult;
                    System.out.println("Using unaccented search results");
                }
            }
            
            // Test với query đơn giản để debug
            if (!keyword.isEmpty() && (result == null || result.size() == 0)) {
                System.out.println("=== TESTING SIMPLE QUERY ===");
                List<NhanVien> testResult = nhanVienRepository.findByTenNVContainingIgnoreCase(keyword);
                System.out.println("Simple query found: " + testResult.size() + " employees");
                for (NhanVien nv : testResult) {
                    System.out.println("- " + nv.getMaNV() + ": '" + nv.getTenNV() + "'");
                }
                
                // Debug: In ra tất cả nhân viên trong database
                System.out.println("=== ALL EMPLOYEES IN DATABASE ===");
                List<NhanVien> allEmployees = nhanVienRepository.getAllForDebug();
                System.out.println("Total employees: " + allEmployees.size());
                for (NhanVien nv : allEmployees) {
                    String name = nv.getTenNV();
                    System.out.println("- " + nv.getMaNV() + ": '" + name + "' (bytes: " + java.util.Arrays.toString(name.getBytes("UTF-8")) + ")");
                }
                
                // Test với từ không dấu
                System.out.println("=== TESTING ASCII KEYWORDS ===");
                String[] testKeywords = {"hanh", "tuan", "minh"};
                for (String testKeyword : testKeywords) {
                    List<NhanVien> asciiResult = nhanVienRepository.findByTenNVContainingIgnoreCase(testKeyword);
                    System.out.println("Keyword '" + testKeyword + "' found: " + asciiResult.size() + " employees");
                    for (NhanVien nv : asciiResult) {
                        System.out.println("  - " + nv.getMaNV() + ": '" + nv.getTenNV() + "'");
                    }
                }
                System.out.println("================================");
            }
            
            return result;
        } catch (Exception e) {
            System.err.println("Lỗi tìm kiếm nhân viên: " + e.getMessage());
            e.printStackTrace();
            // Fallback: trả về tất cả nhân viên nếu tìm kiếm lỗi
            return nhanVienRepository.findAllNotDeleted();
        }
    }
}