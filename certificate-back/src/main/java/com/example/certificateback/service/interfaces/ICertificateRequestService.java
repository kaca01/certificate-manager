package com.example.certificateback.service.interfaces;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.dto.ErrorDTO;

public interface ICertificateRequestService {

    public AllDTO<CertificateRequestDTO> getUserRequests();

    AllDTO<CertificateRequestDTO> getAllRequests();

    CertificateDTO acceptRequest(Long id);

    CertificateRequestDTO refuseRequest(Long id, ErrorDTO reason);

    public CertificateRequestDTO insert(CertificateRequestDTO certificateRequestDTO);
}
