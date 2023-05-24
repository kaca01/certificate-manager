package com.example.certificateback.controller;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.dto.ErrorDTO;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "https://localhost:4200")
@RequestMapping("/api/certificate-request")
public class CertificateRequestController {

    @Autowired
    ICertificateRequestService service;

    private static final Logger logger = LoggerFactory.getLogger(CertificateRequestController.class);


    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AllDTO<CertificateRequestDTO>> getUserRequests() {
        logger.info("All certificate requests for user returned.");
        return new ResponseEntity<>(service.getUserRequests(), HttpStatus.OK);
    }

    @GetMapping(value = "/issuer", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<AllDTO<CertificateRequestDTO>> getRequestsBasedOnIssuer() {
        logger.info("Requests based on issuer returned.");
        return new ResponseEntity<>(service.getRequestsBasedOnIssuer(), HttpStatus.OK);
    }

    @GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AllDTO<CertificateRequestDTO>> getAllRequests() {
        logger.info("All certificate requests returned.");
        return new ResponseEntity<>(service.getAllRequests(), HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateRequestDTO> create(@RequestBody CertificateRequestDTO certificateRequestDTO) {
        return new ResponseEntity<>(service.insert(certificateRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/accept/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateDTO> acceptRequest(@PathVariable Long id) {
        CertificateDTO certificate = service.acceptRequest(id);
        return new ResponseEntity<>(certificate, HttpStatus.OK);
    }

    @PutMapping(value = "/refuse/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateRequestDTO> refuseRequest(@PathVariable Long id, @RequestBody ErrorDTO reason) {
        logger.info("Certificate request refuse action started.");
        CertificateRequestDTO request = service.refuseRequest(id, reason);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}
