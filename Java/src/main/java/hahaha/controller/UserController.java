package hahaha.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import hahaha.model.Account;
import hahaha.repository.AccountRepository;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private AccountRepository accountRepository;
    @GetMapping("/home/{id}/{username}")
    public String homePage(Model model, @PathVariable Long id,@PathVariable String username) {
        Account acc = accountRepository.findByAccountId(id);
        String hoTen = (acc != null && acc.getKhachHang() != null) ? acc.getKhachHang().getHoTen() : "";
        model.addAttribute("accountId", id);
        model.addAttribute("username", username);
        model.addAttribute("hoTen", hoTen);
        return "User/home"; 
    }
}
