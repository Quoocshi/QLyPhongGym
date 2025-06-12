package hahaha.controller;

import java.util.List;
import java.util.ArrayList;

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

import hahaha.model.Account;
import hahaha.model.CaTap;
import hahaha.model.ChiTietDangKyDichVu;
import hahaha.model.KhuVuc;
import hahaha.model.LichTap;
import hahaha.model.Lop;
import hahaha.model.NhanVien;
import hahaha.repository.AccountRepository;
import hahaha.repository.CaTapRepository;
import hahaha.repository.KhuVucRepository;
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
            
            // Tạm thời comment để debug
            // List<KhuVuc> dsKhuVuc = khuVucRepository.findAvailableKhuVuc();
            List<KhuVuc> dsKhuVuc = new ArrayList<>(); // Tạm thời để trống
            System.out.println("Số khu vực: " + (dsKhuVuc != null ? dsKhuVuc.size() : "null"));
            
            // Thêm thông tin vào model
            model.addAttribute("dsPTCustomers", dsPTCustomers);
            model.addAttribute("dsPTSchedules", dsPTSchedules);
            model.addAttribute("dsCaTap", dsCaTap);
            model.addAttribute("dsKhuVuc", dsKhuVuc);
            model.addAttribute("trainer", trainer);
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
                                       @RequestParam String thu,
                                       @RequestParam String caTap,
                                       @RequestParam(required = false) String maKV) {
        try {
            // Lấy thông tin trainer
            Account account = accountRepository.findByAccountId(accountId);
            if (account == null || account.getNhanVien() == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin huấn luyện viên");
            }
            
            String maNV = account.getNhanVien().getMaNV();
            
            // Tạo lịch PT
            LichTap lichTap = lichTapService.createPTSchedule(maNV, maKH, thu, caTap, maKV);
            
            if (lichTap != null) {
                return ResponseEntity.ok("Tạo lịch PT thành công!");
            } else {
                return ResponseEntity.badRequest().body("Không thể tạo lịch PT. Vui lòng kiểm tra lại thông tin.");
            }
            
        } catch (Exception e) {
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
}

