package hahaha.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import hahaha.model.Customer;
import hahaha.service.CustomerServiceImpl;



@RestController
@RequestMapping("/customer")
public class CustomerManagementController {
    @Autowired
    private CustomerServiceImpl customerService;

    @GetMapping("/list")
    public List<Customer> getAllCustomers() {
        return customerService.getAll();
    }

    @PostMapping("/add")
    public String addCustomer(@RequestBody Customer customer){
        customerService.createCustomer(customer);
        return "Đã thêm thành công khách hàng";
    }

    @PutMapping("/update")
    public String updateCustomer(@RequestBody Customer customer){
        customerService.updateCustomer(customer);
        return "Đã cập nhật thành công khách hàng";
    }
    
    @DeleteMapping("/delete")
    public String deleteCustomer(@RequestParam String MaKH){
        customerService.deleteCustomer(MaKH);
        return "Đã xóa thành công khách hàng";
    }
    
    
}
