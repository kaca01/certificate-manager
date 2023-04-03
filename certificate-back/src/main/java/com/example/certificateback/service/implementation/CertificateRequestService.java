package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.enumeration.RequestType;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.ICertificateRequestRepository;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.ICertificateGeneratorService;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    private ICertificateRequestRepository certificateRequestRepository;
    @Autowired
    private ICertificateGeneratorService certificateGeneratorService;

    @Autowired IUserRepository userRepository;

    @Override
    @Transactional
    public AllDTO<CertificateRequestDTO> get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) throw new NotFoundException("User not found!");
        List<CertificateRequest> certificateRequests = certificateRequestRepository.findBySubjectId(user.getId());

        List<CertificateRequestDTO> certificateRequestDTOS = new ArrayList<>();
        for (CertificateRequest certificate : certificateRequests)
            certificateRequestDTOS.add(new CertificateRequestDTO(certificate));

        return new AllDTO<>(certificateRequestDTOS);
    }

    @Override
    public Certificate acceptRequest(Long id) {
        CertificateRequest request = certificateRequestRepository.findById(id).get();
        //todo check validity of issuer certificate
        request.setRequestType(RequestType.ACCEPTED);
        certificateRequestRepository.save(request);

        return certificateGeneratorService.generateCertificate(request);
    }
}
