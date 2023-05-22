package com.example.certificateback.security;

import com.example.certificateback.domain.User;
import com.example.certificateback.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class ActiveSessions {

    @Autowired
    TokenUtils tokenUtils;

    private Map<String, String> users;    // Key = email, value = token


    public String hasSession(String email){
        if (users.containsKey(email)) return users.get(email);
        return null;
    }

    public Boolean isTokenValid(String token, User user){
        Boolean isValid = tokenUtils.checkTokenForActiveSession(token, user);
        if (isValid) return !hasExpired(token, user);
        else {
            users.remove(user.getEmail());
            return false;
        }
    }

    private Boolean hasExpired(String token, User user){
        Date expirationDate = tokenUtils.getExpirationDateFromToken(token);
        if (expirationDate.before(new Date())){
            users.remove(user.getEmail());
            return true;
        }
        //token hasn't expired
        return false;
    }
}
