package hahaha.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hahaha.model.Customer;
import hahaha.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService{
    @Autowired
    CustomerRepository customerRepository;
    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Boolean createCustomer(Customer customer) {
        try{
            customerRepository.save(customer);
            return true;

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;

    }

    @Override
    public Boolean updateCustomer(Customer customer) {
        try{
            customerRepository.save(customer);
            return true;

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean deleteCustomer(String MaKH) {
        try{
            customerRepository.deleteById(MaKH);
            return true;

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Customer findById(String id) {
        return customerRepository.findById(id).get();
    }

}
