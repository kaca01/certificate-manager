package com.example.certificateback.security;

import com.example.certificateback.domain.User;
import com.example.certificateback.util.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ActiveSessions {

    @Autowired
    TokenUtils tokenUtils;

    private final Map<String, String> users = new HashMap<>();    // Key = email, value = token


    public String hasSession(String email){
        if (users.containsKey(email)) return users.get(email);
        return null;
    }

    public void addSession(String token, String email){
        users.put(email, token);
    }

    public Boolean isTokenValid(String token, User user){
        try {
            Boolean isValid = tokenUtils.checkTokenForActiveSession(token, user);
            if (isValid) return !hasExpired(token, user);
            else {
                users.remove(user.getEmail());
                return false;
            }
        } catch (ExpiredJwtException e){
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
