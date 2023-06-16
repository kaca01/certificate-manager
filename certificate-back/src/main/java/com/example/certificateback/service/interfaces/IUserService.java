package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.User;
import com.example.certificateback.dto.ErrorDTO;
import com.example.certificateback.dto.LoginDTO;
import com.example.certificateback.dto.ResetPasswordDTO;
import com.example.certificateback.dto.UserDTO;

import java.util.List;

public interface IUserService {
    User findByEmail(String username);
    List<UserDTO> findAll ();
    UserDTO register(UserDTO registrationDTO);
    ErrorDTO activateUser(Long activationId);
    void sendResetEmail(String email);
    void resetEmail(String email, ResetPasswordDTO resetPasswordDTO);
    void sendSMS(String phone);
    void checkSMS(String phone, ResetPasswordDTO resetPasswordDTO);

    void checkLogin(LoginDTO loginDTO);

    void confirmLogin(LoginDTO loginDTO);
}
