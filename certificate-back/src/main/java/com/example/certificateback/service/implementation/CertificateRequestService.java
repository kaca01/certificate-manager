package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.repository.ICertificateRequestRepository;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    private ICertificateRequestRepository repository;

    @Override
    public AllDTO<CertificateRequestDTO> get() {
        // only simulation because login is not yet implemented
        // TODO : replace this with real user id
        long id = 1;
        List<CertificateRequest> certificateRequests = repository.findBySubjectId(id);

        List<CertificateRequestDTO> certificateRequestDTOS = new ArrayList<>();
        for (CertificateRequest certificate : certificateRequests)
            certificateRequestDTOS.add(new CertificateRequestDTO(certificate));

        return new AllDTO<>(certificateRequestDTOS);
    }
}
