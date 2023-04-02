package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.User;
import com.example.certificateback.dto.UserDTO;

import java.util.List;

public interface IUserService {
    User findById(Long id);
    User findByEmail(String username);
    List<UserDTO> findAll ();
    UserDTO register(UserDTO registrationDTO);
}
