package hahaha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import hahaha.model.Account;
import hahaha.model.Lop;
import hahaha.model.NhanVien;
import hahaha.repository.AccountRepository;
import hahaha.service.LopService;

@Controller
@RequestMapping("/trainer")
public class LichLopController {
    
    @Autowired
    private LopService lopService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @GetMapping("/lichlop")
    public String lichDayLop() {
        return "Trainer/lichlop";
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
}

