package com.example.certificateback.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Password {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password", nullable=false)
    private String password;

    @Column(name = "last_password_reset_date", nullable=false)
    private Date lastPasswordResetDate;

    public Password(String password){
        this.password = password;
        this.lastPasswordResetDate = new Date();
    }
}
