package com.example.certificateback.controller;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
@RequestMapping("/api/certificate-request")
public class CertificateRequestController {

    @Autowired
    ICertificateRequestService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<AllDTO<CertificateRequestDTO>> get() {
        AllDTO<CertificateRequestDTO> dtos = service.get();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
