package com.bandi.customeraccountmanage.repository;


import com.bandi.customeraccountmanage.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}