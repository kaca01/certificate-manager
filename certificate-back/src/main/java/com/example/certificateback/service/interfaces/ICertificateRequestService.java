package com.example.certificateback.service.interfaces;

import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.dto.UserDTO;

public interface ICertificateRequestService {

    public AllDTO<CertificateRequestDTO> get();

    public CertificateRequestDTO insert(CertificateRequestDTO certificateRequestDTO);
}
