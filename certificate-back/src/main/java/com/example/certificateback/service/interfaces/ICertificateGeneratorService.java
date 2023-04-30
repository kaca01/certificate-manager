package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.dto.CertificateDTO;

public interface ICertificateGeneratorService {
    CertificateDTO generateCertificate(CertificateRequest request);
}
