package hahaha.service;

import java.util.List;

import hahaha.model.Customer;

public interface  CustomerService {
    List<Customer> getAll();
    Boolean createCustomer(Customer customer);
    Boolean updateCustomer(Customer customer);
    Boolean deleteCustomer(String MaKH);
}
