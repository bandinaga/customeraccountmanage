package com.bandi.customeraccountmanage.repository;


import com.bandi.customeraccountmanage.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNumber(String accountNumber);
}