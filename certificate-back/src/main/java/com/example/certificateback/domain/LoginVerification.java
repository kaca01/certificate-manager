package com.example.certificateback.domain;

import com.example.certificateback.configuration.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.Random;

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

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "life", nullable = false)
    private int life;

    @Column(name = "code")
    private String code;

    public LoginVerification(User user) {
        int number = new Random().nextInt(999999);
        this.code = String.format("%06d", number);
        this.user = user;
        this.date = new Date();
        this.life = ApplicationConstants.LOGIN_VERIFICATION_LIFE_IN_SECONDS;
    }
}
