//package hahaha.controller;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import hahaha.service.AutocompleteService;
//
//@RestController
//@RequestMapping("/api/autocomplete")
//public class AutocompleteController {
//
//    @Autowired
//    private AutocompleteService autocompleteService;
//
//    /**
//     * Gợi ý tên khách hàng khi tìm kiếm
//     */
//    @GetMapping("/khachhang")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
//    public ResponseEntity<List<String>> suggestKhachHang(@RequestParam("q") String query) {
//        if (query.length() < 2) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<String> suggestions = autocompleteService.suggestKhachHangNames(query);
//        return ResponseEntity.ok(suggestions);
//    }
//
//    /**
//     * Gợi ý email dựa trên pattern thông thường
//     */
//    @GetMapping("/email")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
//    public ResponseEntity<List<String>> suggestEmail(@RequestParam("q") String query) {
//        if (query.length() < 3) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<String> suggestions = autocompleteService.suggestEmailDomains(query);
//        return ResponseEntity.ok(suggestions);
//    }
//
//    /**
//     * Gợi ý địa chỉ dựa trên các địa chỉ có sẵn
//     */
//    @GetMapping("/diachi")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
//    public ResponseEntity<List<String>> suggestDiaChi(@RequestParam("q") String query) {
//        if (query.length() < 3) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<String> suggestions = autocompleteService.suggestDiaChi(query);
//        return ResponseEntity.ok(suggestions);
//    }
//
//    /**
//     * Gợi ý tên nhân viên cho việc tìm kiếm và phân công
//     */
//    @GetMapping("/nhanvien")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
//    public ResponseEntity<List<String>> suggestNhanVien(@RequestParam("q") String query,
//                                                        @RequestParam(value = "loaiNV", required = false) String loaiNV) {
//        if (query.length() < 2) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<String> suggestions = autocompleteService.suggestNhanVienNames(query, loaiNV);
//        return ResponseEntity.ok(suggestions);
//    }
//
//    /**
//     * Gợi ý tên dịch vụ
//     */
//    @GetMapping("/dichvu")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
//    public ResponseEntity<List<String>> suggestDichVu(@RequestParam("q") String query,
//                                                      @RequestParam(value = "maBM", required = false) String maBM) {
//        if (query.length() < 2) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<String> suggestions = autocompleteService.suggestDichVuNames(query, maBM);
//        return ResponseEntity.ok(suggestions);
//    }
//
//    /**
//     * Gợi ý số điện thoại theo format Việt Nam
//     */
//    @GetMapping("/sodienthoai")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
//    public ResponseEntity<List<String>> suggestSoDienThoai(@RequestParam("q") String query) {
//        if (query.length() < 3) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<String> suggestions = autocompleteService.suggestPhoneNumberFormats(query);
//        return ResponseEntity.ok(suggestions);
//    }
//
//    /**
//     * Gợi ý username dựa trên họ tên
//     */
//    @GetMapping("/username")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
//    public ResponseEntity<List<String>> suggestUsername(@RequestParam("hoTen") String hoTen) {
//        if (hoTen.length() < 3) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<String> suggestions = autocompleteService.generateUsernameSuggestions(hoTen);
//        return ResponseEntity.ok(suggestions);
//    }
//
//    /**
//     * Gợi ý trainer theo bộ môn
//     */
//    @GetMapping("/trainer")
//    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'USER')")
//    public ResponseEntity<List<AutocompleteService.TrainerSuggestion>> suggestTrainer(
//            @RequestParam("q") String query,
//            @RequestParam(value = "maBM", required = false) String maBM) {
//        if (query.length() < 2) {
//            return ResponseEntity.ok(List.of());
//        }
//        List<AutocompleteService.TrainerSuggestion> suggestions =
//            autocompleteService.suggestTrainers(query, maBM);
//        return ResponseEntity.ok(suggestions);
//    }
//}