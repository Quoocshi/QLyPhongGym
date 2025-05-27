// package hahaha.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;

// import hahaha.model.KhachHang;
// import hahaha.service.CustomerServiceImpl;

// @Controller 
// @RequestMapping("/customer")
// public class CustomerManagementController {
//     @Autowired
//     private CustomerServiceImpl customerService;

//     @GetMapping("/list")
//     @PreAuthorize("hasRole('ADMIN')")
//     public String getAllCustomers(Model model) {
//         List<KhachHang> customers = customerService.getAll();
//         System.out.println(customers);
//         model.addAttribute("customers", customers);
//         return "Admin/Customer/list";
//     }
//     @GetMapping("/add")
//     @PreAuthorize("hasRole('ADMIN')")
//     public String addCustomerForm(Model model) {
//         model.addAttribute("customer", new KhachHang());
//         return "Admin/Customer/add";
//     }

//     @PostMapping("/add")
//     @PreAuthorize("hasRole('ADMIN')")
//     public String addCustomer(KhachHang customer){
//         customerService.createCustomer(customer);
//         return "redirect:/customer/list"; 
//     }

//     @GetMapping("/update/{id}")
//     @PreAuthorize("hasRole('ADMIN')")
//     public String updateCustomerForm(@PathVariable String id, Model model) {
//         KhachHang customer = customerService.findById(id);
//         model.addAttribute("customer", customer);
//         return "Admin/Customer/update";
//     }
    
//     @PostMapping("/update/{id}")
//     @PreAuthorize("hasRole('ADMIN')")
//     public String updateCustomer(KhachHang customer){
//         customerService.updateCustomer(customer);
//         return "redirect:/customer/list"; 
//     }

//     @PostMapping("/delete")
//     @PreAuthorize("hasRole('ADMIN')")
//     public String deleteCustomer(@RequestParam String MaKH){
//         customerService.deleteCustomer(MaKH);
//         return "redirect:/customer/list"; 
//     }
// }
