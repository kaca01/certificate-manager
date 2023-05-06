package com.example.certificateback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResetPasswordDTO {
    private String newPassword;
    private String firstRepeatPassword;
    private String secondRepeatPassword;
    private String code;

    // request
    public ResetPasswordDTO(String newPassword, String firstRepeatPassword, String secondRepeatPassword, String code) {
        this.newPassword = newPassword;
        this.firstRepeatPassword = firstRepeatPassword;
        this.secondRepeatPassword = secondRepeatPassword;
        this.code = code;
    }
}
