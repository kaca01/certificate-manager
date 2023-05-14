package com.example.certificateback.controller;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.FileDTO;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.service.interfaces.ICertificateService;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.ContentType;
import java.util.Arrays;
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

    @GetMapping(value = "/accept/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CertificateDTO> acceptRequest(@PathVariable Long id) {
        CertificateDTO certificate = certificateRequestService.acceptRequest(id);
        return new ResponseEntity<>(certificate, HttpStatus.OK);
    }

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

    // here is not get method because, in angular, http get method does not support request body
    @PostMapping(value = "/verify/copy")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Boolean isCertificateValidByCopy(@RequestBody FileDTO file) {
        System.out.println("FILE00");
        System.out.println(file.getBytes());

        String[] parts = file.getBytes().split(",");

        byte[] byteArray = new byte[parts.length];

        for (int i = 0; i < parts.length; i++) {
            System.out.println("i" + parts[i]);
//            String value = parts[i].split(":")[1].trim();
            int intValue = Integer.parseInt(parts[i]);

            // Perform range check
            if (intValue < Byte.MIN_VALUE || intValue > Byte.MAX_VALUE) {
                throw new IllegalArgumentException("Value out of range: " + intValue);
            }

            byteArray[i] = (byte) intValue;
        }

        System.out.println(Arrays.toString(byteArray));
//        return false;
        return certificateService.isValidByCopy(byteArray);
    }

    @GetMapping("/issuers")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<AllDTO<CertificateDTO>> getIssuers() {
        return new ResponseEntity<>(certificateService.getIssuers(), HttpStatus.OK);
    }
}
