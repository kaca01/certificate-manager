package com.example.certificateback.dto;

import com.example.certificateback.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

// A DTO that retrieves data from a HTML registration form
@NoArgsConstructor
@Data
public class UserDTO {

    private String name;
    private String surname;
    private String phone;
    private String country;

    private String password;
    private String email;

    public UserDTO(User dto){
        this.country = dto.getCountry();
        this.phone = dto.getPhone();
        this.surname = dto.getSurname();
        this.name = dto.getName();
        this.email = dto.getEmail();
    }
}

