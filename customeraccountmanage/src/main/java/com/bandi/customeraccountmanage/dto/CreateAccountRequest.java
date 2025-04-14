package com.bandi.customeraccountmanage.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateAccountRequest {

    @NotNull(message = "Customer ID cannot be null")
    private Long customerId;

    @NotNull(message = "Account type cannot be null")
    @Pattern(regexp = "Saving|Current", message = "Account type must be either 'Saving' or 'Current'")
    private String accountType;


}
