package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.ksData.IssuerData;
import com.example.certificateback.domain.ksData.SubjectData;

import java.security.cert.X509Certificate;

public interface ICertificateGeneratorService {
    Certificate generateCertificate(CertificateRequest request);
}
