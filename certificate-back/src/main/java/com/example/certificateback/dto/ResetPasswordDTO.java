package com.example.certificateback.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ResetPasswordDTO {
    private String newPassword;

    private String repeatPassword;
    private String code;

    // request
    public ResetPasswordDTO(String newPassword, String repeatPassword,String code) {
        this.newPassword = newPassword;
        this.repeatPassword = repeatPassword;
        this.code = code;
    }
}
