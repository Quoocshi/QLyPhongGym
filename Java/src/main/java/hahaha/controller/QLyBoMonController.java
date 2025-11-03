package hahaha.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import hahaha.model.BoMon;
import hahaha.service.BoMonService;

@RestController
@RequestMapping("api/quan-ly-bo-mon")
public class QLyBoMonController {

    @Autowired
    private BoMonService boMonService;

    @GetMapping("/danh-sach-bo-mon")
    @PreAuthorize("hasRole('ADMIN')")
    public List<BoMon> getDanhSachBoMon() {
        return boMonService.getAllBoMon();
    }

//    @GetMapping("/them-bo-mon")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String showThemBoMonForm(Model model) {
//        model.addAttribute("boMon", new BoMon());
//        model.addAttribute("nextMaBM", boMonService.generateNextMaBoMon());
//        return "Admin/BoMon/add";
//    }

    @PostMapping("/them-bo-mon")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean themBoMon(@RequestBody BoMon boMon) {
        return boMonService.createBoMon(boMon);
    }

//    @GetMapping("/cap-nhat-bo-mon/{maBM}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String showCapNhatForm(@PathVariable String maBM, Model model) {
//        BoMon boMon = boMonService.findByid(maBM);
//        model.addAttribute("boMon", boMon);
//        return "Admin/BoMon/update";
//    }

    @PutMapping("/cap-nhat-bo-mon/{maBM}")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean capNhatBoMon(@PathVariable String maBM, @RequestBody BoMon boMon) {
        boMon.setMaBM(maBM);
        return boMonService.updateBoMon(boMon);
    }
    //xóa bị dính ràng buộc
    @DeleteMapping("/xoa-bo-mon/{maBM}")
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean xoaBoMon(@PathVariable String maBM) {
        return boMonService.deleteBoMon(maBM);
    }
}