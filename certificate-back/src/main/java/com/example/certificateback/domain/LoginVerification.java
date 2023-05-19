package com.example.certificateback.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class LoginVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "expired_date", nullable = false)
    private Date expiredDate;

    @Column(name = "code")
    private String code;

    public LoginVerification(User user, Date expiredDate, String code) {
        this.user = user;
        this.expiredDate = expiredDate;
        this.code = code;
    }
}
