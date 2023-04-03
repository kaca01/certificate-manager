package com.example.certificateback.controller;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    ICertificateRequestService certificateRequestService;

    @GetMapping(value = "/accept", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Certificate> acceptRequest(@PathVariable Long id) {
        Certificate certificate = certificateRequestService.acceptRequest(id);
        return new ResponseEntity<>(certificate, HttpStatus.OK);
    }
}
