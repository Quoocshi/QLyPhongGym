package hahaha.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.model.KhachHang;
import hahaha.model.NhanVien;
import hahaha.model.DichVu;
import hahaha.repository.KhachHangRepository;
import hahaha.repository.NhanVienRepository;
import hahaha.repository.DichVuRepository;

@Service
public class AutocompleteService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private DichVuRepository dichVuRepository;

    /**
     * Gợi ý tên khách hàng dựa trên input
     */
    public List<String> suggestKhachHangNames(String query) {
        String normalizedQuery = normalizeVietnamese(query.toLowerCase());
        
        return khachHangRepository.findAllWithActiveAccount().stream()
                .map(KhachHang::getHoTen)
                .filter(name -> name != null)
                .filter(name -> {
                    String normalizedName = normalizeVietnamese(name.toLowerCase());
                    return normalizedName.contains(normalizedQuery) || 
                           containsAllWords(normalizedName, normalizedQuery);
                })
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý email domains phổ biến
     */
    public List<String> suggestEmailDomains(String query) {
        List<String> commonDomains = Arrays.asList(
                "@gmail.com", "@yahoo.com", "@hotmail.com", "@outlook.com",
                "@student.uit.edu.vn", "@uit.edu.vn", "@email.com"
        );

        String localPart = query.contains("@") ? query.split("@")[0] : query;
        
        return commonDomains.stream()
                .map(domain -> localPart + domain)
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý địa chỉ dựa trên các địa chỉ có sẵn trong hệ thống
     */
    public List<String> suggestDiaChi(String query) {
        String normalizedQuery = normalizeVietnamese(query.toLowerCase());
        
        // Lấy các địa chỉ đã có trong hệ thống
        Set<String> existingAddresses = new HashSet<>();
        
        // Từ khách hàng
        khachHangRepository.findAllWithActiveAccount().stream()
                .map(KhachHang::getDiaChi)
                .filter(addr -> addr != null && !addr.trim().isEmpty())
                .forEach(existingAddresses::add);

        // Thêm một số địa chỉ mẫu phổ biến tại TP.HCM
        existingAddresses.addAll(Arrays.asList(
                "Quận 1, TP. Hồ Chí Minh",
                "Quận 3, TP. Hồ Chí Minh", 
                "Quận 5, TP. Hồ Chí Minh",
                "Quận 7, TP. Hồ Chí Minh",
                "Quận 10, TP. Hồ Chí Minh",
                "Quận Thủ Đức, TP. Hồ Chí Minh",
                "Quận Bình Thạnh, TP. Hồ Chí Minh",
                "Quận Tân Bình, TP. Hồ Chí Minh",
                "Quận Phú Nhuận, TP. Hồ Chí Minh",
                "Quận Gò Vấp, TP. Hồ Chí Minh"
        ));

        return existingAddresses.stream()
                .filter(addr -> {
                    String normalizedAddr = normalizeVietnamese(addr.toLowerCase());
                    return normalizedAddr.contains(normalizedQuery);
                })
                .distinct()
                .limit(8)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý tên nhân viên theo loại
     */
    public List<String> suggestNhanVienNames(String query, String loaiNV) {
        String normalizedQuery = normalizeVietnamese(query.toLowerCase());
        
        List<NhanVien> nhanViens = nhanVienRepository.findAll();
        
        return nhanViens.stream()
                .filter(nv -> loaiNV == null || nv.getLoaiNV().toString().equals(loaiNV))
                .map(NhanVien::getTenNV)
                .filter(name -> name != null)
                .filter(name -> {
                    String normalizedName = normalizeVietnamese(name.toLowerCase());
                    return normalizedName.contains(normalizedQuery) ||
                           containsAllWords(normalizedName, normalizedQuery);
                })
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý tên dịch vụ theo bộ môn
     */
    public List<String> suggestDichVuNames(String query, String maBM) {
        String normalizedQuery = normalizeVietnamese(query.toLowerCase());
        
        List<DichVu> dichVus = dichVuRepository.findAll();
        
        return dichVus.stream()
                .filter(dv -> maBM == null || dv.getBoMon().getMaBM().equals(maBM))
                .map(DichVu::getTenDV)
                .filter(name -> name != null)
                .filter(name -> {
                    String normalizedName = normalizeVietnamese(name.toLowerCase());
                    return normalizedName.contains(normalizedQuery);
                })
                .distinct()
                .limit(8)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý format số điện thoại Việt Nam
     */
    public List<String> suggestPhoneNumberFormats(String query) {
        List<String> suggestions = new ArrayList<>();
        
        // Loại bỏ các ký tự không phải số
        String digitsOnly = query.replaceAll("[^0-9]", "");
        
        if (digitsOnly.length() >= 3) {
            // Format với +84
            if (digitsOnly.startsWith("84") && digitsOnly.length() >= 5) {
                String formatted = "+84 " + digitsOnly.substring(2, Math.min(5, digitsOnly.length())) 
                                 + (digitsOnly.length() > 5 ? " " + digitsOnly.substring(5) : "");
                suggestions.add(formatted);
            }
            
            // Format với 0
            if (digitsOnly.startsWith("0") && digitsOnly.length() >= 4) {
                String formatted = digitsOnly.substring(0, Math.min(4, digitsOnly.length()))
                                 + (digitsOnly.length() > 4 ? " " + digitsOnly.substring(4) : "");
                suggestions.add(formatted);
            }
            
            // Nếu chưa có prefix, thêm gợi ý
            if (!digitsOnly.startsWith("0") && !digitsOnly.startsWith("84")) {
                suggestions.add("0" + digitsOnly);
                suggestions.add("+84 " + digitsOnly);
            }
        }
        
        return suggestions.stream().limit(3).collect(Collectors.toList());
    }

    /**
     * Generate username suggestions từ họ tên
     */
    public List<String> generateUsernameSuggestions(String hoTen) {
        List<String> suggestions = new ArrayList<>();
        
        if (hoTen == null || hoTen.trim().isEmpty()) {
            return suggestions;
        }
        
        String normalized = normalizeVietnamese(hoTen.toLowerCase())
                           .replaceAll("[^a-z\\s]", "")
                           .trim();
        
        String[] words = normalized.split("\\s+");
        
        if (words.length >= 2) {
            String lastName = words[words.length - 1];
            String firstName = words[0];
            
            // Các pattern phổ biến
            suggestions.add(firstName + "." + lastName);
            suggestions.add(firstName + lastName);
            suggestions.add(lastName + firstName);
            suggestions.add(firstName + "_" + lastName);
            
            // Với số
            suggestions.add(firstName + lastName + "01");
            suggestions.add(firstName + lastName + "123");
            
            // Chỉ tên
            if (firstName.length() >= 3) {
                suggestions.add(firstName);
                suggestions.add(firstName + "01");
            }
        }
        
        return suggestions.stream()
                .distinct()
                .limit(6)
                .collect(Collectors.toList());
    }

    /**
     * Gợi ý trainer theo bộ môn với thông tin chi tiết
     */
    public List<TrainerSuggestion> suggestTrainers(String query, String maBM) {
        String normalizedQuery = normalizeVietnamese(query.toLowerCase());
        
        List<NhanVien> trainers = nhanVienRepository.findTrainersByBoMon(maBM);
        
        return trainers.stream()
                .filter(trainer -> {
                    String normalizedName = normalizeVietnamese(trainer.getTenNV().toLowerCase());
                    return normalizedName.contains(normalizedQuery) ||
                           trainer.getMaNV().toLowerCase().contains(query.toLowerCase());
                })
                .map(trainer -> new TrainerSuggestion(
                        trainer.getMaNV(),
                        trainer.getTenNV(),
                        trainer.getEmail(),
                        maBM
                ))
                .limit(8)
                .collect(Collectors.toList());
    }

    /**
     * Chuẩn hóa tiếng Việt để tìm kiếm tốt hơn
     */
    private String normalizeVietnamese(String input) {
        if (input == null) return "";
        
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }

    /**
     * Kiểm tra xem text có chứa tất cả các từ trong query không
     */
    private boolean containsAllWords(String text, String query) {
        String[] queryWords = query.split("\\s+");
        String[] textWords = text.split("\\s+");
        
        for (String queryWord : queryWords) {
            boolean found = false;
            for (String textWord : textWords) {
                if (textWord.startsWith(queryWord)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    /**
     * Inner class cho trainer suggestion với thông tin chi tiết
     */
    public static class TrainerSuggestion {
        private String maNV;
        private String tenNV;
        private String email;
        private String maBM;

        public TrainerSuggestion(String maNV, String tenNV, String email, String maBM) {
            this.maNV = maNV;
            this.tenNV = tenNV;
            this.email = email;
            this.maBM = maBM;
        }

        // Getters and setters
        public String getMaNV() { return maNV; }
        public void setMaNV(String maNV) { this.maNV = maNV; }
        
        public String getTenNV() { return tenNV; }
        public void setTenNV(String tenNV) { this.tenNV = tenNV; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getMaBM() { return maBM; }
        public void setMaBM(String maBM) { this.maBM = maBM; }
    }
} 