package com.example.certificateback.controller;

import com.example.certificateback.configuration.ApplicationConstants;
import com.example.certificateback.domain.LoginVerification;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.*;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.IUserService;
import com.example.certificateback.util.TokenUtils;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.twilio.example.ValidationExample.ACCOUNT_SID;
import static com.twilio.example.ValidationExample.AUTH_TOKEN;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    IUserService service;
    @Autowired
    IUserRepository userRepository;
    @Autowired
    TokenUtils tokenUtils;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        UserDTO user = service.register(userDTO);
        return new ResponseEntity<UserDTO>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/checkLogin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> checkLogin(@RequestBody LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new BadRequestException("Wrong username or password!"));
        if(!user.getEmail().equals(loginDTO.getEmail()) || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()))
            throw new BadRequestException("Wrong username or password!");

        // checking password expiring
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getLastPasswordResetDate());
        calendar.add(Calendar.DAY_OF_YEAR, ApplicationConstants.RESET_PASSWORD_TIMEOUT_IN_DAYS);
        if(calendar.getTime().before(new Date()))
            throw new BadRequestException("Password has expired!");

        service.checkLogin(loginDTO);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenStateDTO> login(@RequestBody LoginDTO loginDTO) {

        service.confirmLogin(loginDTO);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create tokens for that user
        User user = (User) authentication.getPrincipal();
        String access = tokenUtils.generateToken(user.getEmail());
        int expiresIn = tokenUtils.getExpiredIn();

        return new ResponseEntity<>(new TokenStateDTO(access, expiresIn), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AllDTO<UserDTO>> getUsers()
    {
        List<UserDTO> usersDTO = service.findAll();

        AllDTO<UserDTO> allUsers = new AllDTO<>(usersDTO);

        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    //activate user account
    @GetMapping(value = "/activate/{activationId}")
    public ResponseEntity<ErrorDTO> activateUser(@PathVariable int activationId)
    {
        ErrorDTO message = service.activateUser((long) activationId);
        return new ResponseEntity<ErrorDTO>(message, HttpStatus.OK);
    }
    
    // Reset password of user
    @GetMapping(value = "/{email}/resetPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendResetEmail(@PathVariable String email) throws MessagingException, UnsupportedEncodingException {
        service.sendResetEmail(email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Change password of a user with the reset code
    @PutMapping(value = "/{email}/resetPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(@PathVariable String email, @RequestBody ResetPasswordDTO resetPasswordDTO)
    {
        service.resetEmail(email, resetPasswordDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{phone}/sendSMS")
    public ResponseEntity<String> sendSMS(@PathVariable String phone){
        service.sendSMS(phone);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{phone}/sendSMS")
    public ResponseEntity<?> checkSMS(@PathVariable String phone, @RequestBody ResetPasswordDTO resetPasswordDTO) {
        service.checkSMS(phone, resetPasswordDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/currentUser")
    public User user(Principal user) {
        return this.service.findByEmail(user.getName());
    }

}
