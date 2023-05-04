package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.User;
import com.example.certificateback.dto.ResetPasswordDTO;
import com.example.certificateback.dto.UserDTO;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface IUserService {
    User findById(Long id);
    User findByEmail(String username);
    List<UserDTO> findAll ();
    UserDTO register(UserDTO registrationDTO);
    void sendResetEmail(String email) throws MessagingException, UnsupportedEncodingException;
    void resetEmail(String email, ResetPasswordDTO resetPasswordDTO);
}
