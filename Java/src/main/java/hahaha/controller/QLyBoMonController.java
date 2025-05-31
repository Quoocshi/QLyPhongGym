package hahaha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import hahaha.model.BoMon;
import hahaha.service.BoMonService;

@Controller
@RequestMapping("/quan-ly-bo-mon")
public class QLyBoMonController {
    
    @Autowired
    private BoMonService boMonService;
    
    @GetMapping("/danh-sach-bo-mon")
    @PreAuthorize("hasRole('ADMIN')")
    public String getDanhSachBoMon(Model model) {
        List<BoMon> listBoMon = boMonService.getAllBoMon();
        model.addAttribute("listBoMon", listBoMon);
        return "Admin/BoMon/qlybm";
    }

    @GetMapping("/them-bo-mon")
    @PreAuthorize("hasRole('ADMIN')")
    public String showThemBoMonForm(Model model) {
        model.addAttribute("boMon", new BoMon());
        model.addAttribute("nextMaBM", boMonService.generateNextMaBoMon());
        return "Admin/BoMon/add";
    }

    @PostMapping("/them-bo-mon")
    @PreAuthorize("hasRole('ADMIN')")
    public String themBoMon(@ModelAttribute BoMon boMon) {
        boMonService.createBoMon(boMon);
        return "redirect:/quan-ly-bo-mon/danh-sach-bo-mon";  

    }

    @GetMapping("/cap-nhat-bo-mon/{maBM}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCapNhatForm(@PathVariable String maBM, Model model) {
        BoMon boMon = boMonService.findByid(maBM);
        model.addAttribute("boMon", boMon);
        return "Admin/BoMon/update";
    }

    @PostMapping("/cap-nhat-bo-mon/{maBM}")
    @PreAuthorize("hasRole('ADMIN')")
    public String capNhatBoMon(@PathVariable String maBM, @ModelAttribute BoMon boMon) {
        boMon.setMaBM(maBM);
        boMonService.updateBoMon(boMon);
        return "redirect:/quan-ly-bo-mon/danh-sach-bo-mon";
    }

    @PostMapping("/xoa-bo-mon")
    @PreAuthorize("hasRole('ADMIN')")
    public String xoaBoMon(@RequestParam String maBM) {
        boMonService.deleteBoMon(maBM);
        return "redirect:/quan-ly-bo-mon/danh-sach-bo-mon";
    }
}