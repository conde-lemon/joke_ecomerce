package com.example.ecommerce.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data // Reemplaza todos los getters y setters
public class OrderForm {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @Email @NotBlank private String email;
    @NotBlank private String phone;
    @NotBlank private String address;
    @NotBlank private String city;

    @NotBlank private String paymentMethod; // tarjeta | yape | plin
    private String cardNumber;
    private String cardName;
    private String cvv;
}