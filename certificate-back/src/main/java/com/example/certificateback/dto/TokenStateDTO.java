package com.example.certificateback.dto;

import lombok.Data;

// A DTO that encapsulates the generated JWT and its duration that are returned to the client
@Data
public class TokenStateDTO {
	
    private String accessToken;
    private Long expiresIn;

    public TokenStateDTO() {
        this.accessToken = null;
        this.expiresIn = null;
    }

    public TokenStateDTO(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}