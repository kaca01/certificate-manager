package com.example.certificateback.controller;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin
@RequestMapping("/api/certificate-request")
public class CertificateRequestController {

    @Autowired
    ICertificateRequestService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<AllDTO<CertificateRequestDTO>> get() {
        return new ResponseEntity<>(service.get(), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    // TODO : for some reason doesn't work with authorization
    public ResponseEntity<CertificateRequestDTO> create(@RequestBody CertificateRequestDTO certificateRequestDTO) {
        return new ResponseEntity<>(service.insert(certificateRequestDTO), HttpStatus.CREATED);
    }
}
