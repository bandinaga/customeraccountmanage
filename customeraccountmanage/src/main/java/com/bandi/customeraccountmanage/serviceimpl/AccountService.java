package com.bandi.customeraccountmanage.serviceimpl;

import com.bandi.customeraccountmanage.dto.CreateAccountRequest;
import com.bandi.customeraccountmanage.entity.Account;
import com.bandi.customeraccountmanage.entity.AuditLog;
import com.bandi.customeraccountmanage.entity.Customer;
import com.bandi.customeraccountmanage.repository.AccountRepository;
import com.bandi.customeraccountmanage.repository.AuditLogRepository;
import com.bandi.customeraccountmanage.repository.CustomerRepository;
import com.bandi.customeraccountmanage.util.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    // Create Account
    @Transactional
    public Account createAccount(CreateAccountRequest createAccountRequest) {
        Customer customer = customerRepository.findById(createAccountRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + createAccountRequest.getCustomerId() + " not found"));

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(createAccountRequest.getAccountType());
        account.setBalance(0.0);
        account.setStatus("Active");  // Default status

        // Save account to repository
        Account savedAccount = accountRepository.save(account);

        // Create audit log
        AuditLog auditLog = new AuditLog(
            savedAccount.getAccountNumber(),
            "CREATE",
            "Account created with type: " + createAccountRequest.getAccountType(),
            0.0,
            0.0,
            "Active"
        );
        auditLogRepository.save(auditLog);

        return savedAccount;
    }

    private String generateAccountNumber() {
        return "AC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Deposit Cash
    @Transactional
    public Account depositCash(String accountNumber, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account ==  null) {
            throw new ResourceNotFoundException("Account with number '" + accountNumber + "' not found");
        }
        if (account.getStatus().equals("Closed")) {
            throw new IllegalArgumentException("Account with number '" + accountNumber + "' is closed");
        }

        account.setBalance(account.getBalance() + amount);
        Account updatedAccount = accountRepository.save(account);

        // Create audit log for deposit
        AuditLog auditLog = new AuditLog(
            accountNumber,
            "DEPOSIT",
            "Cash deposit",
            amount,
            updatedAccount.getBalance(),
            updatedAccount.getStatus()
        );
        auditLogRepository.save(auditLog);

        return updatedAccount;
    }

    // Withdraw Cash
    @Transactional
    public Account withdrawCash(String accountNumber, Double amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account ==  null) {
            throw new ResourceNotFoundException("Account with number '" + accountNumber + "' not found");
        }
        if (account.getStatus().equals("Active") && account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            Account updatedAccount = accountRepository.save(account);

            // Create audit log for withdrawal
            AuditLog auditLog = new AuditLog(
                accountNumber,
                "WITHDRAW",
                "Cash withdrawal",
                amount,
                updatedAccount.getBalance(),
                updatedAccount.getStatus()
            );
            auditLogRepository.save(auditLog);

            return updatedAccount;
        }
        throw new IllegalArgumentException("Insufficient balance or account is closed.");
    }

    // Close Account
    @Transactional
    public Account closeAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new ResourceNotFoundException("Account with number '" + accountNumber + "' not found");
        }
        // Check if account is already closed
        if ("Closed".equalsIgnoreCase(account.getStatus())) {
            throw new IllegalArgumentException("Account with number '" + accountNumber + "' is already closed");
        }
        account.setStatus("Closed");
        Account updatedAccount = accountRepository.save(account);

        // Create audit log for account closure
        AuditLog auditLog = new AuditLog(
            accountNumber,
            "CLOSE",
            "Account closed",
            0.0,
            updatedAccount.getBalance(),
            "Closed"
        );
        auditLogRepository.save(auditLog);

        return updatedAccount;
    }

    // Inquire Account
    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account != null) {
            // Create audit log for account inquiry
            AuditLog auditLog = new AuditLog(
                accountNumber,
                "INQUIRY",
                "Account balance inquiry",
                0.0,
                account.getBalance(),
                account.getStatus()
            );
            auditLogRepository.save(auditLog);
        }
        return account;
    }

    // Get Account Audit History
    @Transactional(readOnly = true)
    public List<AuditLog> getAccountAuditHistory(String accountNumber) {
        return auditLogRepository.findByAccountNumberOrderByTimestampDesc(accountNumber);
    }
}






