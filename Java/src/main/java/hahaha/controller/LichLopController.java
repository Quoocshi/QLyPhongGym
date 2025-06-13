package hahaha.controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpSession;

import hahaha.model.Account;
import hahaha.model.CaTap;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.KhuVuc;
import hahaha.model.LichTap;
import hahaha.model.Lop;
import hahaha.model.NhanVien;
import hahaha.repository.AccountRepository;
import hahaha.repository.CaTapRepository;
import hahaha.repository.ChiTietDangKyDichVuRepository;
import hahaha.repository.KhuVucRepository;
import hahaha.repository.LichTapRepository;
import hahaha.repository.LopRepository;
import hahaha.repository.NhanVienRepository;
import hahaha.service.LichTapService;
import hahaha.service.LopService;

@Controller
@RequestMapping("/trainer")
public class LichLopController {
    
    @Autowired
    private LopService lopService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private LichTapService lichTapService;
    
    @Autowired
    private CaTapRepository caTapRepository;
    
    @Autowired
    private KhuVucRepository khuVucRepository;
    
    @Autowired
    private ChiTietDangKyDichVuRepository chiTietDangKyDichVuRepository;
    
    @Autowired
    private LichTapRepository lichTapRepository;
    
    @Autowired
    private LopRepository lopRepository;
    
    @Autowired
    private NhanVienRepository nhanVienRepository;
    
    @GetMapping("/lichlop")
    public String lichDayLop() {
        return "Trainer/lichlop";
    }
    
    @GetMapping("/lichcanhan")
    public String lichDayCanHanSimple() {
        return "Trainer/lichcanhan";
    }
    
    @GetMapping("/lichlop/{accountId}/{username}")
    public String lichDayLopWithParams(@PathVariable Long accountId, @PathVariable String username, Model model) {
        try {
            // Lấy thông tin account để tìm trainer
            Account account = accountRepository.findByAccountId(accountId);
            if (account == null || account.getNhanVien() == null) {
                model.addAttribute("error", "Không tìm thấy thông tin huấn luyện viên");
                return "Trainer/lichlop";
            }
            
            NhanVien trainer = account.getNhanVien();
            String maNV = trainer.getMaNV();
            
            // Lấy danh sách lớp của trainer hiện tại
            List<Lop> dsLopCuaTrainer = lopService.getLopsByTrainerMaNV(maNV);
            
            // Thêm thông tin vào model
            model.addAttribute("dsLop", dsLopCuaTrainer);
            model.addAttribute("trainer", trainer);
            model.addAttribute("accountId", accountId);
            model.addAttribute("username", username);
            model.addAttribute("lopService", lopService);
            
            return "Trainer/lichlop";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "Trainer/lichlop";
        }
    }
    
    @GetMapping("/lichcanhan/{accountId}/{username}")
    public String lichDayCanHan(@PathVariable Long accountId, @PathVariable String username, Model model) {
        try {
            System.out.println("=== DEBUG lichDayCanHan ===");
            System.out.println("AccountId: " + accountId);
            System.out.println("Username: " + username);
            
            // Lấy thông tin account để tìm trainer
            Account account = accountRepository.findByAccountId(accountId);
            if (account == null || account.getNhanVien() == null) {
                model.addAttribute("error", "Không tìm thấy thông tin huấn luyện viên");
                return "Trainer/lichcanhan";
            }
            
            NhanVien trainer = account.getNhanVien();
            String maNV = trainer.getMaNV();
            
            System.out.println("Trainer: " + trainer.getTenNV() + " (" + maNV + ")");
            
            // Lấy danh sách khách hàng đã đăng ký PT với trainer
            List<ChiTietDangKyDichVu> dsPTCustomers = lichTapService.getPTCustomersByTrainer(maNV);
            System.out.println("Số khách hàng PT: " + (dsPTCustomers != null ? dsPTCustomers.size() : "null"));
            
            // Lấy lịch PT hiện tại của trainer
            List<LichTap> dsPTSchedules = lichTapService.getPTScheduleByTrainer(maNV);
            System.out.println("Số lịch PT: " + (dsPTSchedules != null ? dsPTSchedules.size() : "null"));
            
            // Lấy danh sách ca tập
            List<CaTap> dsCaTap = caTapRepository.findAll();
            System.out.println("Số ca tập: " + (dsCaTap != null ? dsCaTap.size() : "null"));
            
            // Lấy danh sách khu vực có sẵn
            List<KhuVuc> dsKhuVuc = khuVucRepository.findAll();
            System.out.println("Số khu vực: " + (dsKhuVuc != null ? dsKhuVuc.size() : "null"));
            
            // Thêm thông tin vào model
            model.addAttribute("dsPTCustomers", dsPTCustomers);
            model.addAttribute("dsPTSchedules", dsPTSchedules);
            model.addAttribute("dsCaTap", dsCaTap);
            model.addAttribute("dsKhuVuc", dsKhuVuc);
            model.addAttribute("trainer", trainer);
            model.addAttribute("maNV", maNV);
            model.addAttribute("accountId", accountId);
            model.addAttribute("username", username);
            
            System.out.println("=== Trả về template: Trainer/lichcanhan ===");
            return "Trainer/lichcanhan";
        } catch (Exception e) {
            System.err.println("=== LỖI trong lichDayCanHan ===");
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "Trainer/lichcanhan";
        }
    }
    
    @PostMapping("/tao-lich-pt")
    @ResponseBody
    public ResponseEntity<?> taoLichPT(@RequestParam Long accountId,
                                       @RequestParam String maKH,
                                       @RequestParam String ngayTap,
                                       @RequestParam String caTap,
                                       @RequestParam(required = false) String maKV,
                                       RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== TẠO LỊCH PT ===");
            System.out.println("AccountId: " + accountId);
            System.out.println("MaKH: " + maKH);
            System.out.println("NgayTap: " + ngayTap);
            System.out.println("CaTap: " + caTap);
            System.out.println("MaKV: " + maKV);
            
            // Lấy thông tin trainer
            Account account = accountRepository.findByAccountId(accountId);
            if (account == null || account.getNhanVien() == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin huấn luyện viên");
            }
            
            String maNV = account.getNhanVien().getMaNV();
            System.out.println("Trainer MaNV: " + maNV);
            
            // Tạo lịch PT với ngày cụ thể
            LichTap lichTap = lichTapService.createPTScheduleWithDate(maNV, maKH, ngayTap, caTap, maKV);
            
            if (lichTap != null) {
                System.out.println("✅ Tạo lịch PT thành công: " + lichTap.getMaLT());
                return ResponseEntity.ok("Tạo lịch PT thành công!");
            } else {
                System.err.println("❌ Không thể tạo lịch PT");
                return ResponseEntity.badRequest().body("Không thể tạo lịch PT. Vui lòng kiểm tra lại thông tin.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo lịch PT: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    
    @PostMapping("/kiem-tra-xung-dot")
    @ResponseBody
    public ResponseEntity<?> kiemTraXungDot(@RequestParam Long accountId,
                                            @RequestParam String thu,
                                            @RequestParam String caTap) {
        try {
            // Lấy thông tin trainer
            Account account = accountRepository.findByAccountId(accountId);
            if (account == null || account.getNhanVien() == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin huấn luyện viên");
            }
            
            String maNV = account.getNhanVien().getMaNV();
            
            // Kiểm tra xung đột
            boolean hasConflict = lichTapService.hasScheduleConflict(maNV, thu, caTap);
            
            return ResponseEntity.ok().body("{\"hasConflict\":" + hasConflict + "}");
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    
    @PostMapping("/createPTScheduleWithDate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createPTScheduleWithDate(
            @RequestParam String maNV,
            @RequestParam String maKH,
            @RequestParam String ngayTap,
            @RequestParam String caTap,
            @RequestParam(required = false) String maKV,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== DEBUG CREATE PT SCHEDULE ===");
            System.out.println("Request trainer: " + maNV);
            System.out.println("Customer: " + maKH);
            System.out.println("Date: " + ngayTap);
            System.out.println("Time slot: " + caTap);
            System.out.println("Area: " + maKV);
            
            // Debug session attributes
            System.out.println("=== SESSION DEBUG ===");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session isNew: " + session.isNew());
            System.out.println("All session attributes:");
            java.util.Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                Object value = session.getAttribute(name);
                System.out.println("  " + name + " = " + value);
            }
            
            // Kiểm tra quyền truy cập thông qua account trong session
            Object accountIdObj = session.getAttribute("accountId");
            System.out.println("Session accountId: " + accountIdObj);
            
            if (accountIdObj == null) {
                System.out.println("❌ No accountId in session");
                response.put("success", false);
                response.put("message", "Phiên đăng nhập đã hết hạn");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Lấy thông tin account từ session
            Long accountId = Long.valueOf(accountIdObj.toString());
            Account account = accountRepository.findByAccountId(accountId);
            
            if (account == null || account.getNhanVien() == null) {
                System.out.println("❌ Account not found or not a trainer");
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin huấn luyện viên");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            String sessionMaNV = account.getNhanVien().getMaNV();
            System.out.println("Session trainer from account: " + sessionMaNV);
            
            // Kiểm tra trainer có khớp không
            if (!sessionMaNV.equals(maNV)) {
                System.out.println("❌ Trainer mismatch: session=" + sessionMaNV + ", request=" + maNV);
                response.put("success", false);
                response.put("message", "Không có quyền truy cập");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            System.out.println("✅ Authorization passed");
            
            // Tạo lịch PT
            LichTap lichTap = lichTapService.createPTScheduleWithDate(maNV, maKH, ngayTap, caTap, maKV);
            
            if (lichTap != null) {
                System.out.println("✅ Successfully created PT schedule: " + lichTap.getMaLT());
                response.put("success", true);
                response.put("message", "Tạo lịch PT thành công!");
                response.put("data", lichTap.getMaLT());
            } else {
                System.out.println("❌ Failed to create PT schedule");
                response.put("success", false);
                response.put("message", "Không thể tạo lịch PT. Vui lòng kiểm tra lại thông tin.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Exception in createPTScheduleWithDate: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Debug endpoint to check PT customer data
    @GetMapping("/debug/ptCustomers/{maNV}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> debugPTCustomers(@PathVariable String maNV) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== DEBUG PT CUSTOMERS FOR TRAINER: " + maNV + " ===");
            
            List<ChiTietDangKyDichVu> ptCustomers = chiTietDangKyDichVuRepository.findPTCustomersByTrainer(maNV);
            System.out.println("Found " + (ptCustomers != null ? ptCustomers.size() : 0) + " PT customers");
            
            List<Map<String, Object>> customerData = new ArrayList<>();
            
            if (ptCustomers != null) {
                for (ChiTietDangKyDichVu pt : ptCustomers) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("maCTDK", pt.getMaCTDK());
                    data.put("maKH", pt.getHoaDon().getKhachHang().getMaKH());
                    data.put("tenKH", pt.getHoaDon().getKhachHang().getHoTen());
                    data.put("ngayBD", pt.getNgayBD());
                    data.put("ngayKT", pt.getNgayKT());
                    data.put("tenDV", pt.getDichVu().getTenDV());
                    data.put("trangThaiHD", pt.getHoaDon().getTrangThai());
                    
                    customerData.add(data);
                    
                    System.out.println("Customer: " + pt.getHoaDon().getKhachHang().getHoTen() + 
                                     " (" + pt.getHoaDon().getKhachHang().getMaKH() + ")");
                    System.out.println("  Service: " + pt.getDichVu().getTenDV());
                    System.out.println("  Period: " + pt.getNgayBD() + " - " + pt.getNgayKT());
                    System.out.println("  Invoice status: " + pt.getHoaDon().getTrangThai());
                }
            }
            
            response.put("success", true);
            response.put("data", customerData);
            
        } catch (Exception e) {
            System.err.println("❌ Exception in debugPTCustomers: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Simple test endpoint without CSRF for debugging
    @PostMapping("/testCreatePTSchedule")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> testCreatePTSchedule(
            @RequestParam String maNV,
            @RequestParam String maKH,
            @RequestParam String ngayTap,
            @RequestParam String caTap,
            @RequestParam(required = false) String maKV) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== TEST CREATE PT SCHEDULE (NO CSRF) ===");
            System.out.println("Trainer: " + maNV);
            System.out.println("Customer: " + maKH);
            System.out.println("Date: " + ngayTap);
            System.out.println("Time slot: " + caTap);
            System.out.println("Area: " + maKV);
            
            // Tạo lịch PT
            LichTap lichTap = lichTapService.createPTScheduleWithDate(maNV, maKH, ngayTap, caTap, maKV);
            
            if (lichTap != null) {
                System.out.println("✅ Successfully created PT schedule: " + lichTap.getMaLT());
                response.put("success", true);
                response.put("message", "Tạo lịch PT thành công!");
                response.put("data", lichTap.getMaLT());
            } else {
                System.out.println("❌ Failed to create PT schedule");
                response.put("success", false);
                response.put("message", "Không thể tạo lịch PT. Vui lòng kiểm tra lại thông tin.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Exception in testCreatePTSchedule: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Lỗi hệ thống: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Debug endpoint to check session
    @GetMapping("/debug/session")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> debugSession(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("=== DEBUG SESSION ENDPOINT ===");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session isNew: " + session.isNew());
            
            Map<String, Object> sessionData = new HashMap<>();
            java.util.Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                Object value = session.getAttribute(name);
                sessionData.put(name, value != null ? value.toString() : "null");
                System.out.println("  " + name + " = " + value);
            }
            
            response.put("success", true);
            response.put("sessionId", session.getId());
            response.put("isNew", session.isNew());
            response.put("attributes", sessionData);
            
        } catch (Exception e) {
            System.err.println("❌ Exception in debugSession: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}

