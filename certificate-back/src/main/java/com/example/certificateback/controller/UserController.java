package com.example.certificateback.controller;

import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.LoginDTO;
import com.example.certificateback.dto.TokenStateDTO;
import com.example.certificateback.dto.UserDTO;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.IUserService;
import com.example.certificateback.util.TokenUtils;
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

import java.security.Principal;
import java.util.List;

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

    @GetMapping("/currentUser")
    public User user(Principal user) {
        return this.service.findByEmail(user.getName());
    }

}
