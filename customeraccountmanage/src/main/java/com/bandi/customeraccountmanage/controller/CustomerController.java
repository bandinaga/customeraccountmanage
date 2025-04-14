package com.bandi.customeraccountmanage.controller;

import com.bandi.customeraccountmanage.dto.CreateAccountRequest;
import com.bandi.customeraccountmanage.entity.Account;
import com.bandi.customeraccountmanage.entity.Customer;
import com.bandi.customeraccountmanage.serviceimpl.AccountService;
import com.bandi.customeraccountmanage.serviceimpl.CustomerService;
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

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Customer Management", description = "APIs for managing customer information")
public class CustomerController {

    @Autowired
    private CustomerService customerService;




    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
                content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            });
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }

        Customer createdCustomer = customerService.createCustomer(customer.getName(), customer.getEmail(), customer.getPhone(), customer.getDateOfBirth());
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }


    @Operation(summary = "Get a customer by ID", description = "Returns a customer based on the provided ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found",
                content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<Customer> getCustomerById(@Parameter(description = "ID of the customer to retrieve") @PathVariable Long id) {
        Optional<Customer> customerOptional = customerService.getCustomerById(id);
        if (customerOptional.isPresent()) {
            return new ResponseEntity<>(customerOptional.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Customer with id " + id + " not found.");
        }
    }


    @Operation(summary = "Get all customers", description = "Returns a list of all customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of customers found",
                content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "No customers found")
    })
    @GetMapping("/getCustomers")
    public ResponseEntity<?> getCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return new ResponseEntity<>("No customers found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(customers, HttpStatus.OK);
        }
    }


    @Operation(summary = "Update a customer", description = "Updates an existing customer's information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully",
                content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @PutMapping("/updateCustomer/{id}")
    public ResponseEntity<?> updateCustomer(
            @Parameter(description = "ID of the customer to update") @PathVariable Long id,
            @Parameter(description = "Updated customer information") @RequestBody Customer updatedCustomer) {
        Optional<Customer> existingCustomerOptional = customerService.getCustomerById(id);

        if (existingCustomerOptional.isPresent()) {
            Customer existingCustomer = existingCustomerOptional.get();
            existingCustomer.setName(updatedCustomer.getName());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setPhone(updatedCustomer.getPhone());
            existingCustomer.setDateOfBirth(updatedCustomer.getDateOfBirth());

            customerService.saveCustomer(existingCustomer);

            return new ResponseEntity<>(existingCustomer, HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found");
        }
    }


    @Operation(summary = "Delete a customer", description = "Deletes a customer by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<?> deleteCustomer(
            @Parameter(description = "ID of the customer to delete") @PathVariable Long id) {
        Optional<Customer> existingCustomerOptional = customerService.getCustomerById(id);

        if (existingCustomerOptional.isPresent()) {
            customerService.deleteCustomerById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found");
        }
    }


}







