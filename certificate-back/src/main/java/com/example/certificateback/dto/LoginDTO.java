package com.example.certificateback.dto;

import lombok.Data;

// A DTO for login
@Data
public class LoginDTO {

    private String email;
    private String password;

    public LoginDTO() {
        super();
    }

    public LoginDTO(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }
}
