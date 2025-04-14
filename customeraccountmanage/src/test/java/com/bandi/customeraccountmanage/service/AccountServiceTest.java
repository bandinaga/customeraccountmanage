package com.bandi.customeraccountmanage.service;

import com.bandi.customeraccountmanage.dto.CreateAccountRequest;
import com.bandi.customeraccountmanage.entity.Account;
import com.bandi.customeraccountmanage.entity.AuditLog;
import com.bandi.customeraccountmanage.entity.Customer;
import com.bandi.customeraccountmanage.repository.AccountRepository;
import com.bandi.customeraccountmanage.repository.AuditLogRepository;
import com.bandi.customeraccountmanage.repository.CustomerRepository;
import com.bandi.customeraccountmanage.serviceimpl.AccountService;
import com.bandi.customeraccountmanage.util.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AccountService accountService;

    private Customer testCustomer;
    private Account testAccount;
    private CreateAccountRequest createAccountRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");

        testAccount = new Account();
        testAccount.setAccountNumber("AC12345678");
        testAccount.setCustomer(testCustomer);
        testAccount.setAccountType("Savings");
        testAccount.setBalance(1000.0);
        testAccount.setStatus("Active");

        createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setCustomerId(1L);
        createAccountRequest.setAccountType("Savings");
    }

    @Test
    void createAccount_ShouldCreateAccountAndAuditLog() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        Account result = accountService.createAccount(createAccountRequest);

        assertNotNull(result);
        assertEquals("Savings", result.getAccountType());
        assertEquals(testCustomer, result.getCustomer());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void depositCash_ShouldUpdateBalanceAndCreateAuditLog() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        Account result = accountService.depositCash("AC12345678", 500.0);

        assertNotNull(result);
        assertEquals(1500.0, result.getBalance());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void withdrawCash_ShouldUpdateBalanceAndCreateAuditLog() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        Account result = accountService.withdrawCash("AC12345678", 500.0);

        assertNotNull(result);
        assertEquals(500.0, result.getBalance());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void closeAccount_ShouldUpdateStatusAndCreateAuditLog() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(new AuditLog());

        Account result = accountService.closeAccount("AC12345678");

        assertNotNull(result);
        assertEquals("Closed", result.getStatus());
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void getAccountAuditHistory_ShouldReturnAuditLogs() {
        List<AuditLog> auditLogs = Arrays.asList(
            new AuditLog("AC12345678", "CREATE", "Account created", 0.0, 0.0, "Active"),
            new AuditLog("AC12345678", "DEPOSIT", "Cash deposit", 1000.0, 1000.0, "Active")
        );

        when(auditLogRepository.findByAccountNumberOrderByTimestampDesc("AC12345678"))
            .thenReturn(auditLogs);

        List<AuditLog> result = accountService.getAccountAuditHistory("AC12345678");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CREATE", result.get(0).getOperation());
        assertEquals("DEPOSIT", result.get(1).getOperation());
    }

    @Test
    void createAccount_ShouldThrowException_WhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.createAccount(createAccountRequest);
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }

    @Test
    void withdrawCash_ShouldThrowException_WhenInsufficientBalance() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(testAccount);

        assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdrawCash("AC12345678", 2000.0);
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(auditLogRepository, never()).save(any(AuditLog.class));
    }
}