package com.example.certificateback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecaptchaResponse {
    private Boolean success;
    private String challenge_ts;
    private String hostname;
    private Double score;
    private String action;
}
