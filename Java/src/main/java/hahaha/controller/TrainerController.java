//package hahaha.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/trainer")
//public class TrainerController {
//
//    @GetMapping("/home/{id}/{username}")
//    public String trainerPage(Model model, @PathVariable Long id, @PathVariable String username) {
//        model.addAttribute("userId", id);
//        model.addAttribute("username", username);
//        return "Trainer/home";
//    }
//}
