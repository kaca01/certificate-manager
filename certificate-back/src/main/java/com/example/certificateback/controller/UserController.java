package com.example.certificateback.controller;

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

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenStateDTO> login(@RequestBody LoginDTO loginDTO) {
        User check = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(() -> new BadRequestException("Wrong username or password!"));
        if(!check.getEmail().equals(loginDTO.getEmail()) || !passwordEncoder.matches(loginDTO.getPassword(), check.getPassword()))
            throw new BadRequestException("Wrong username or password!");

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Create tokens for that user
        User user = (User) authentication.getPrincipal();
        String access = tokenUtils.generateToken(user.getEmail());
        int expiresIn = tokenUtils.getExpiredIn();

        return new ResponseEntity<>(new TokenStateDTO(access, expiresIn), HttpStatus.OK);
    }

    // todo check if this works
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AllDTO<UserDTO>> getUsers()
    {
        List<UserDTO> usersDTO = service.findAll();

        AllDTO<UserDTO> allUsers = new AllDTO<>(usersDTO);

        return new ResponseEntity<>(allUsers, HttpStatus.OK);
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

    @GetMapping(value = "/generateOTP")
    public ResponseEntity<String> generateOTP(){

        Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));

        Verification verification = Verification.creator(
                        "VA831f2696b6027535bdc7de2892d591eb", // this is your verification sid
                        "+381621164208", //this is your Twilio verified recipient phone number
                        "sms") // this is your channel type
                .create();

        System.out.println(verification.getStatus());

//        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());

        return new ResponseEntity<>("Your OTP has been sent to your verified phone number", HttpStatus.OK);
    }

    @GetMapping("/verifyOTP")
    public ResponseEntity<?> verifyUserOTP() throws Exception {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(
                            "VA831f2696b6027535bdc7de2892d591eb")
                    .setTo("+381621164208")
                    .setCode("486578")
                    .create();

            System.out.println(verificationCheck.getStatus());

        } catch (Exception e) {
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("This user's verification has been completed successfully", HttpStatus.OK);
    }

    @GetMapping("/currentUser")
    public User user(Principal user) {
        return this.service.findByEmail(user.getName());
    }

}
