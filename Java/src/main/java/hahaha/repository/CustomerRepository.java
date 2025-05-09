package hahaha.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import hahaha.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {}
