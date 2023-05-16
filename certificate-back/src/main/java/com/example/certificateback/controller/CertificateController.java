package com.example.certificateback.controller;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.service.interfaces.ICertificateService;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    ICertificateService certificateService;

    @Autowired
    IUserService userService;

    @Autowired
    ICertificateRequestService certificateRequestService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AllDTO<CertificateDTO>> getAllCertificates()
    {
        List<CertificateDTO> certificatesDTO = certificateService.getAllCertificates();
        AllDTO<CertificateDTO> allMyCertificates = new AllDTO<>(certificatesDTO);
        return new ResponseEntity<>(allMyCertificates, HttpStatus.OK);
    }

    @GetMapping("/verify/{serialNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Boolean isCertificateValid(@PathVariable String serialNumber) {
        return certificateService.checkingValidation(serialNumber);
    }

    @GetMapping("/issuers")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AllDTO<CertificateDTO>> getIssuers() {
        return new ResponseEntity<>(certificateService.getIssuers(), HttpStatus.OK);
    }

    @PutMapping(value = "/invalidate/{serialNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateDTO> invalidateCertificate(@PathVariable String serialNumber, @RequestBody String
                                                                withdrawnReason) {
        return new ResponseEntity<>(certificateService.invalidate(serialNumber, withdrawnReason), HttpStatus.OK);
    }

    @GetMapping(value = "downloadCert/{serialNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> downloadCertificate(@PathVariable String serialNumber) {
        ByteArrayResource file = certificateService.downloadCertificate(serialNumber);
        return new ResponseEntity<>(file, HttpStatus.OK);
    }

    @GetMapping(value = "downloadPk/{serialNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> downloadPrivateKey(@PathVariable String serialNumber) {
        ByteArrayResource file = certificateService.downloadPrivateKey(serialNumber);
        return new ResponseEntity<>(file, HttpStatus.OK);
    }
}
