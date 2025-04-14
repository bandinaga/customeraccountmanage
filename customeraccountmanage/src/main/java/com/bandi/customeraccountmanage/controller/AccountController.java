package com.bandi.customeraccountmanage.controller;

import com.bandi.customeraccountmanage.dto.CreateAccountRequest;
import com.bandi.customeraccountmanage.entity.Account;
import com.bandi.customeraccountmanage.serviceimpl.AccountService;
import com.bandi.customeraccountmanage.util.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Account Management", description = "APIs for managing bank accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Create a new account", description = "Creates a new bank account for a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully",
                content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            });
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
        Account account = accountService.createAccount(createAccountRequest);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    @Operation(summary = "Deposit cash", description = "Deposits cash into a specified account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cash deposited successfully",
                content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/depositCash")
    public Account depositCash(
            @Parameter(description = "Account number") @RequestParam String accountNumber,
            @Parameter(description = "Amount to deposit") @RequestParam Double amount) {
        return accountService.depositCash(accountNumber, amount);
    }

    @Operation(summary = "Withdraw cash", description = "Withdraws cash from a specified account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cash withdrawn successfully",
                content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "Account not found"),
        @ApiResponse(responseCode = "400", description = "Insufficient balance")
    })
    @PostMapping("/withdrawCash")
    public Account withdrawCash(
            @Parameter(description = "Account number") @RequestParam String accountNumber,
            @Parameter(description = "Amount to withdraw") @RequestParam Double amount) {
        return accountService.withdrawCash(accountNumber, amount);
    }

    @Operation(summary = "Close account", description = "Closes a specified bank account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account closed successfully",
                content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PostMapping("/closeAccount")
    public Account closeAccount(
            @Parameter(description = "Account number to close") @RequestParam String accountNumber) {
        return accountService.closeAccount(accountNumber);
    }

    @Operation(summary = "Get account details", description = "Retrieves account details by account number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account details retrieved successfully",
                content = @Content(schema = @Schema(implementation = Account.class))),
        @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/getAccount/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(
            @Parameter(description = "Account number to retrieve") @PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        if (account == null) {
            throw new ResourceNotFoundException("Account with number '" + accountNumber + "' not found");
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}