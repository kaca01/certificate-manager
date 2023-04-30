package com.example.certificateback.service.interfaces;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.dto.CertificateRequestDTO;

public interface ICertificateRequestService {

    public AllDTO<CertificateRequestDTO> get();

    CertificateDTO acceptRequest(Long id);
    public CertificateRequestDTO insert(CertificateRequestDTO certificateRequestDTO);
}
