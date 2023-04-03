package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.Role;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.dto.UserDTO;
import com.example.certificateback.enumeration.CertificateType;
import com.example.certificateback.enumeration.RequestType;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.ICertificateRepository;
import com.example.certificateback.repository.ICertificateRequestRepository;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    private ICertificateRequestRepository repository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICertificateRepository certificateRepository;

    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) throw new NotFoundException("User not found!");
        return user;
    }

    @Override
    @Transactional
    public AllDTO<CertificateRequestDTO> get() {
        User user = getLoggedUser();
        List<CertificateRequest> certificateRequests = repository.findBySubjectId(user.getId());

        List<CertificateRequestDTO> certificateRequestDTOS = new ArrayList<>();
        for (CertificateRequest certificate : certificateRequests)
            certificateRequestDTOS.add(new CertificateRequestDTO(certificate));

        return new AllDTO<>(certificateRequestDTOS);
    }

    private CertificateRequestDTO acceptCertificate(CertificateRequest request) {
        CertificateRequestDTO dto = new CertificateRequestDTO(request);
        repository.save(request);
        // TODO : here needs to be called function that will create certificate
        dto.setSubject(request.getSubject().getId());
        return dto;
    }

    private void checkForExceptions(CertificateRequest request) {

    }

    @Override
    public CertificateRequestDTO insert(CertificateRequestDTO certificateRequestDTO) {
        // TODO : also later check if certificate is valid
        // this can be implemented after 7a implementation
        User user = getLoggedUser();
        Role role = user.getRoles().get(0);
        CertificateRequest request = new CertificateRequest(certificateRequestDTO);
        Certificate issuer = certificateRepository.findBySerialNumber(certificateRequestDTO.getIssuer());

        if (issuer == null && request.getCertificateType() != CertificateType.ROOT)
            throw new NotFoundException("Issuer not found!");

        if (request.getCertificateType() != CertificateType.ROOT)
            if (issuer.isWithdrawn()) throw new BadRequestException("Issuer is withdrawn!");

        request.setSubject(user);
        certificateRequestDTO.setSubject(user.getId());
        request.setIssuer(issuer);

        if (request.getCertificateType() != CertificateType.ROOT) {
            if (request.getIssuer().getCertificateType() == CertificateType.END)
                throw new BadRequestException("End certificate can't generate another certificate!");
        }
        if (role.getName().equals("ROLE_ADMIN")) {
            request.setRequestType(RequestType.ACCEPTED);
            return acceptCertificate(request);
        }
        if (request.getCertificateType() == CertificateType.ROOT)
            throw new BadRequestException("User can't ask for the root certificate!");

        if (request.getIssuer().getSubject().getId() == user.getId()) {
            request.setRequestType(RequestType.ACCEPTED);
            return acceptCertificate(request);
        }
        else {
            // TODO : here also call a function that will create certificate
            repository.save(request);
        }
        return certificateRequestDTO;
    }
}
