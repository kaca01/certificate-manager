package com.example.certificateback.controller;

import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.RegistrationDTO;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    IUserService service;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> register(@RequestBody RegistrationDTO userDTO) {
        User user = service.register(userDTO);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AllDTO<RegistrationDTO>> getUsers()
    {
        List<RegistrationDTO> usersDTO = service.findAll();

        AllDTO<RegistrationDTO> allUsers = new AllDTO<>(usersDTO.size(), usersDTO);

        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

}
