package com.example.certificateback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

// A DTO that retrieves data from a HTML registration form
@NoArgsConstructor
@Data
public class RegistrationDTO {

    private String name;
    private String surname;
    private String phone;
    private String country;
    private String password;
    private String email;
}

