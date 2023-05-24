package com.example.certificateback.controller;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.service.interfaces.ICertificateService;
import com.example.certificateback.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://localhost:4200")
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    ICertificateService certificateService;

    @Autowired
    IUserService userService;

    @Autowired
    ICertificateRequestService certificateRequestService;

    private static final Logger logger = LoggerFactory.getLogger(CertificateController.class);


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AllDTO<CertificateDTO>> getAllCertificates()
    {
        logger.info("Getting all certificates.");
        List<CertificateDTO> certificatesDTO = certificateService.getAllCertificates();
        AllDTO<CertificateDTO> allMyCertificates = new AllDTO<>(certificatesDTO);
        logger.info("All certificates returned.");
        return new ResponseEntity<>(allMyCertificates, HttpStatus.OK);
    }

    @GetMapping("/verify/{serialNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Boolean isCertificateValid(@PathVariable String serialNumber) {
        logger.info("Checking certificate validation by serial number.");
        return certificateService.checkingValidation(serialNumber);
    }

    // here is not get method because, in angular, http get method does not support request body
    @PostMapping(value = "/verify/copy")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Boolean isCertificateValidByCopy(@RequestBody String file) {
        String[] parts = file.split(",");

        byte[] byteArray = new byte[parts.length];

        for (int i = 0; i < parts.length; i++) {
            int intValue = Integer.parseInt(parts[i]);

            // Perform range check
            if (intValue < Byte.MIN_VALUE || intValue > Byte.MAX_VALUE) {
                throw new IllegalArgumentException("Value out of range: " + intValue);
            }

            byteArray[i] = (byte) intValue;
        }

        logger.info("Checking certificate validation by copy.");
        return certificateService.isValidByCopy(byteArray);
    }

    @GetMapping("/issuers")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AllDTO<CertificateDTO>> getIssuers() {
        logger.info("System returned issuers.");
        return new ResponseEntity<>(certificateService.getIssuers(), HttpStatus.OK);
    }

    @PutMapping(value = "/invalidate/{serialNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateDTO> invalidateCertificate(@PathVariable String serialNumber, @RequestBody String
                                                                withdrawnReason) {
        logger.info("The certificate has been revoked.");
        return new ResponseEntity<>(certificateService.invalidate(serialNumber, withdrawnReason), HttpStatus.OK);
    }

    @GetMapping(value = "downloadCert/{serialNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> downloadCertificate(@PathVariable String serialNumber) {
        ByteArrayResource file = certificateService.downloadCertificate(serialNumber);
        logger.info("The certificate has been downloaded.");
        return new ResponseEntity<>(file, HttpStatus.OK);
    }

    @GetMapping(value = "downloadPk/{serialNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> downloadPrivateKey(@PathVariable String serialNumber) {
        ByteArrayResource file = certificateService.downloadPrivateKey(serialNumber);
        logger.info("The key has been downloaded.");
        return new ResponseEntity<>(file, HttpStatus.OK);
    }
}
