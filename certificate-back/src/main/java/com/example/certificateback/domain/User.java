package com.example.certificateback.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class User implements UserDetails {

    @Id
    private Long id;
    @Column(name = "email")
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "country")
    private String country;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "phone")
    private String phone;

    @OneToMany
    @JoinColumn(name = "user_id")
    private List<Password> passwords = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public Timestamp getLastPasswordResetDate(){
        Timestamp lastResetDate = null;  //todo set something from distant past
        for (Password p : this.passwords){
            if (p.getLastPasswordResetDate().after(lastResetDate))
            {
                lastResetDate = p.getLastPasswordResetDate();
            }
        }
        return lastResetDate;
    }

    public String getPassword(){
        Timestamp lastResetDate = null;  //todo set something from distant past
        String password = "";
        for (Password p : this.passwords){
            if (p.getLastPasswordResetDate().after(lastResetDate))
            {
                lastResetDate = p.getLastPasswordResetDate();
                password = p.getPassword();
            }
        }
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
