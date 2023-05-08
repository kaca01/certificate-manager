package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface ICertificateService {

    List<CertificateDTO> getAllCertificates();

    Boolean checkingValidation(String serialNumber);

    AllDTO<CertificateDTO> getIssuers();

    void downloadCertificate(String serialNum);
}
