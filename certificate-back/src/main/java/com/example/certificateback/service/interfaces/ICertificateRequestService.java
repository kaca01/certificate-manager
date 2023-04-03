package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateRequestDTO;

public interface ICertificateRequestService {

    public AllDTO<CertificateRequestDTO> get();

    Certificate acceptRequest(Long id);
}
