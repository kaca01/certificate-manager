package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.Role;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.dto.CertificateRequestDTO;
import com.example.certificateback.dto.ErrorDTO;
import com.example.certificateback.enumeration.CertificateType;
import com.example.certificateback.enumeration.RequestType;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.exception.NotValidException;
import com.example.certificateback.exception.WrongUserException;
import com.example.certificateback.repository.ICertificateRepository;
import com.example.certificateback.repository.ICertificateRequestRepository;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.ICertificateGeneratorService;
import com.example.certificateback.service.interfaces.ICertificateRequestService;
import com.example.certificateback.service.interfaces.ICertificateService;
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
    private ICertificateService certificateService;

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

    private AllDTO<CertificateRequestDTO> getRequests(List<CertificateRequest> certificateRequests) {
        List<CertificateRequestDTO> certificateRequestDTOS = new ArrayList<>();
        for (CertificateRequest certificate : certificateRequests)
            certificateRequestDTOS.add(new CertificateRequestDTO(certificate));

        return new AllDTO<>(certificateRequestDTOS);
    }

    @Override
    @Transactional
    public AllDTO<CertificateRequestDTO> getUserRequests() {
        User user = getLoggedUser();
        List<CertificateRequest> certificateRequests = certificateRequestRepository.findBySubjectId(user.getId());

        return getRequests(certificateRequests);
    }

    @Override
    public AllDTO<CertificateRequestDTO> getAllRequests() {
        List<CertificateRequest> certificateRequests = certificateRequestRepository.findAll();
        return getRequests(certificateRequests);
    }

    @Override
    public AllDTO<CertificateRequestDTO> getRequestsBasedOnIssuer() {
        User issuer = getLoggedUser();
        List<CertificateRequest> certificateRequests = certificateRequestRepository.findByRequestTypeAndIssuerSubjectId
                (RequestType.ACTIVE, issuer.getId());
        return getRequests(certificateRequests);
    }

    @Override
    public CertificateDTO acceptRequest(Long id) {
        CertificateRequest request = certificateRequestRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Request does not exist!"));
        checkForExceptions(request);
        // obuhvata i provjeru validacije i withdrawn atributa
        if (request.getCertificateType() != CertificateType.ROOT && !certificateService.checkingValidation(request.getIssuer().getSerialNumber())){
            throw new NotValidException("Issuer certificate is not valid! This request cannot be accepted.");
        }
        checkForRequestExceptions(request);

        request.setRequestType(RequestType.ACCEPTED);
        certificateRequestRepository.save(request);
        return certificateGeneratorService.createCertificate(request);
    }

    @Override
    public CertificateRequestDTO refuseRequest(Long id, ErrorDTO reason) {
        CertificateRequest request = certificateRequestRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Request does not exist!"));
        checkForExceptions(request);
        checkForRequestExceptions(request);
        request.setRequestType(RequestType.REFUSED);
        request.setRefusalReason(reason.getMessage());
        certificateRequestRepository.save(request);
        return new CertificateRequestDTO(request);
    }

    private void checkForRequestExceptions(CertificateRequest request){
        if (!request.getRequestType().equals(RequestType.ACTIVE)){
            throw new NotValidException("Cannot refuse/accept request that is no longer active");
        }
        if (request.getCertificateType() != CertificateType.ROOT && !getLoggedUser().getEmail().equals(request.getIssuer().getSubject().getEmail())){
            throw new WrongUserException("You are not issuer of this certificate!");
        }
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
