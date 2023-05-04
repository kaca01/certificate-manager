package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.Role;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.enumeration.CertificateType;
import com.example.certificateback.enumeration.RequestType;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.ICertificateRepository;
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

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICertificateRepository certificateRepository;

    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) throw new NotFoundException("User not found!");
        //List<CertificateRequest> certificateRequests = certificateRequestRepository.findBySubjectId(user.getId());
        return user;
    }

    @Override
    @Transactional
    public AllDTO<CertificateRequestDTO> get() {
        User user = getLoggedUser();
        List<CertificateRequest> certificateRequests = certificateRequestRepository.findBySubjectId(user.getId());

        List<CertificateRequestDTO> certificateRequestDTOS = new ArrayList<>();
        for (CertificateRequest certificate : certificateRequests)
            certificateRequestDTOS.add(new CertificateRequestDTO(certificate));

        return new AllDTO<>(certificateRequestDTOS);
    }

    @Override
    public CertificateDTO acceptRequest(Long id) {
        //todo check validity?
        CertificateRequest request = certificateRequestRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Request does not exist!"));
        request.setRequestType(RequestType.ACCEPTED);
        certificateRequestRepository.save(request);

        return certificateGeneratorService.createCertificate(request);
    }

    private void checkForExceptions(CertificateRequest request) {
        if (request.getCertificateType() != CertificateType.ROOT) {
            if (request.getIssuer() == null) throw new NotFoundException("Issuer not found!");

            if (request.getIssuer().isWithdrawn()) throw new BadRequestException("Issuer is withdrawn!");

            if (request.getIssuer().getCertificateType() == CertificateType.END)
                throw new BadRequestException("End certificate can't generate another certificate!");
        }
    }

    @Override
    public CertificateRequestDTO insert(CertificateRequestDTO certificateRequestDTO) {
        // TODO : check validity of issuer certificate
        // this can be implemented after 7a implementation
        User user = getLoggedUser();
        Role role = user.getRoles().get(0);
        CertificateRequest request = new CertificateRequest(certificateRequestDTO);
        Certificate issuer;
        if (request.getCertificateType() == CertificateType.ROOT)
            issuer = null;
        else
            issuer = certificateRepository.findBySerialNumber(certificateRequestDTO.getIssuer())
                .orElseThrow(() -> new NotFoundException("Certificate not found!"));
        request.setIssuer(issuer);
        request.setSubject(user);
        request.setRequestType(RequestType.ACTIVE);

        checkForExceptions(request);

        request = certificateRequestRepository.save(request);

        if (role.getName().equals("ROLE_ADMIN")) {
            acceptRequest(request.getId());
            request.setRequestType(RequestType.ACCEPTED);
        }

        else if (request.getCertificateType() == CertificateType.ROOT)
            throw new BadRequestException("User can't ask for the root certificate!");

        else if (request.getIssuer().getSubject().getId() == user.getId()) {
            acceptRequest(request.getId());
            request.setRequestType(RequestType.ACCEPTED);
        }
//
//        else {
//            // TODO : here also call a function that will create certificate    ???
//        }

        request = certificateRequestRepository.save(request);
        return new CertificateRequestDTO(request);
    }
}
