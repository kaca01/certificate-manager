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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@CrossOrigin(origins = "https://localhost:4200")
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

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO) {
        logger.info("User is trying to create new account.");
        UserDTO user = service.register(userDTO);
        logger.info("Request submitted successfully.");
        return new ResponseEntity<UserDTO>(user, HttpStatus.OK);
    }

    @PostMapping(value = "/checkLogin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> checkLogin(@RequestBody LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new BadRequestException("Wrong username or password!"));
        if(!user.getEmail().equals(loginDTO.getEmail()) || !passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            logger.error("Wrong username or password!");
            throw new BadRequestException("Wrong username or password!");
        }

        // checking password expiring
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(user.getLastPasswordResetDate());
        calendar.add(Calendar.DAY_OF_YEAR, ApplicationConstants.RESET_PASSWORD_TIMEOUT_IN_DAYS);
        if(calendar.getTime().before(new Date())) {
            logger.error("Password has expired!");
            throw new BadRequestException("Password has expired!");
        }
        logger.info("Validating login...");
        service.checkLogin(loginDTO);
        logger.info("Login validation successful.");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenStateDTO> login(@RequestBody LoginDTO loginDTO) {
        logger.info("User is trying to log in.");
        service.confirmLogin(loginDTO);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create tokens for that user
        User user = (User) authentication.getPrincipal();
        String access = tokenUtils.generateToken(user.getEmail());
        int expiresIn = tokenUtils.getExpiredIn();

        logger.info("User logged in successfully.");
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
        logger.info("Trying to activate account.");
        ErrorDTO message = service.activateUser((long) activationId);
        logger.info("Account activation successful.");
        return new ResponseEntity<ErrorDTO>(message, HttpStatus.OK);
    }
    
    // Reset password of user
    @GetMapping(value = "/{email}/resetPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendResetEmail(@PathVariable String email) throws MessagingException, UnsupportedEncodingException {
        logger.info("Trying to send code for reset password via mail.");
        service.sendResetEmail(email);
        logger.info("Reset password mail sent successfully.");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Change password of a user with the reset code
    @PutMapping(value = "/{email}/resetPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> resetPassword(@PathVariable String email, @RequestBody ResetPasswordDTO resetPasswordDTO)
    {
        logger.info("Trying to send code via mail for password change.");
        service.resetEmail(email, resetPasswordDTO);
        logger.info("Code sent successfully.");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{phone}/sendSMS")
    public ResponseEntity<String> sendSMS(@PathVariable String phone){
        logger.info("Trying to send mail via SMS.");
        service.sendSMS(phone);
        logger.info("SMS send successfully.");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{phone}/sendSMS")
    public ResponseEntity<?> checkSMS(@PathVariable String phone, @RequestBody ResetPasswordDTO resetPasswordDTO) {
        logger.info("Trying to verify code.");
        service.checkSMS(phone, resetPasswordDTO);
        logger.info("Verification successful.");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/currentUser")
    public User user(Principal user) {
        return this.service.findByEmail(user.getName());
    }

}
