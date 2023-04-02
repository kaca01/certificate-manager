package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.User;
import com.example.certificateback.dto.RegistrationDTO;

import java.util.List;

public interface IUserService {
    User findById(Long id);
    User findByEmail(String username);
    List<User> findAll ();
    User register(RegistrationDTO registrationDTO);
}
