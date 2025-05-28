package hahaha.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trainer")
public class LichLopController {
    @GetMapping("/lichlop")
    public String lichDayLop() {
        return "Trainer/lichlop";
    }
}

