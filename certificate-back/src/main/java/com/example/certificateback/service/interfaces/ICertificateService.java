package com.example.certificateback.service.interfaces;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface ICertificateService {

    List<CertificateDTO> getAllCertificates();

    Boolean checkingValidation(String serialNumber);

    AllDTO<CertificateDTO> getIssuers();

    CertificateDTO invalidate(String serialNumber, String withdrawnReason);
    
    ByteArrayResource downloadCertificate(String seralNumber);

    ByteArrayResource downloadPrivateKey(String serialNumber);
}
