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
import com.example.certificateback.service.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    private ICertificateRequestRepository certificateRequestRepository;

    @Autowired
    private ICertificateGeneratorService certificateGeneratorService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ICertificateService certificateService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICertificateRepository certificateRepository;

    private static final Logger logger = LoggerFactory.getLogger(CertificateRequestService.class);


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
        Long id = userService.getLoggedUser().getId();
        logger.info(String.format("Returned all certificates for a user with id %d.", id));
        return getRequests(certificateRequests);
    }

    @Override
    public AllDTO<CertificateRequestDTO> getAllRequests() {
        List<CertificateRequest> certificateRequests = certificateRequestRepository.findAll();
        Long id = userService.getLoggedUser().getId();
        logger.info(String.format("All certificate requests returned to a user with id %d.", id));
        return getRequests(certificateRequests);
    }

    @Override
    public AllDTO<CertificateRequestDTO> getRequestsBasedOnIssuer() {
        Long id = userService.getLoggedUser().getId();
        logger.info(String.format("Started getting requests based on issuer to a user with id %d.", id));
        User issuer = getLoggedUser();
        List<CertificateRequest> certificateRequests = certificateRequestRepository.findByRequestTypeAndIssuerSubjectId
                (RequestType.ACTIVE, issuer.getId());
        logger.info(String.format("Requests based on issuer returned to a user with id %d.", id));
        return getRequests(certificateRequests);
    }

    @Override
    public CertificateDTO acceptRequest(Long id) {
        try {
            CertificateRequest request = certificateRequestRepository.findById(id).orElseThrow(() ->
                    new NotFoundException("Request does not exist!"));
            checkForCreateCertificateExceptions(request);
            // obuhvata i provjeru validacije i withdrawn atributa
            if (request.getCertificateType() != CertificateType.ROOT && !certificateService.checkingValidation(request.getIssuer().getSerialNumber())) {
                throw new NotValidException("Issuer certificate is not valid! This request cannot be accepted.");
            }
            checkForRequestExceptions(request);

            request.setRequestType(RequestType.ACCEPTED);
            certificateRequestRepository.save(request);
            logger.info(String.format("Request accepted by user with id %d.", id));
            return certificateGeneratorService.createCertificate(request);
        }
        catch (NotFoundException | NotValidException e) {
            if (e.getClass().getName().equals("NotFoundException")) logger.error("Certificate request does not exist.");
            else logger.error("Certificate issuer is not valid.");
            throw e;
        }
    }

    @Override
    public CertificateRequestDTO refuseRequest(Long id, ErrorDTO reason) {
        try {
            CertificateRequest request = certificateRequestRepository.findById(id).orElseThrow(() ->
                    new NotFoundException("Request does not exist!"));
            checkForCreateCertificateExceptions(request);
            checkForRequestExceptions(request);
            request.setRequestType(RequestType.REFUSED);
            request.setRefusalReason(reason.getMessage());
            certificateRequestRepository.save(request);
            logger.info(String.format("Certificate request refused successfully by user with id %d.", id));
            return new CertificateRequestDTO(request);
        } catch (NotFoundException e) {
            logger.error("Certificate request is not found.");
            throw e;
        }
    }

    private void checkForRequestExceptions(CertificateRequest request){
        if (!request.getRequestType().equals(RequestType.ACTIVE)){
            logger.error("Cannot refuse/accept request that is no longer active.");
            throw new NotValidException("Cannot refuse/accept request that is no longer active");
        }
        if (request.getCertificateType() != CertificateType.ROOT && !getLoggedUser().getEmail().equals(request.getIssuer().getSubject().getEmail())
        && !getLoggedUser().getRoles().get(0).getName().equals("ROLE_ADMIN")){
            logger.error("User that is not issuer of the certificate can't execute refuse/accept action.");
            throw new WrongUserException("You are not issuer of this certificate!");
        }
    }

    private void checkForCreateCertificateExceptions(CertificateRequest request) {
        if (request.getCertificateType() != CertificateType.ROOT) {
            if (request.getIssuer() == null) {
                logger.error("Issuer not found.");
                throw new NotFoundException("Issuer not found!");
            }

            if (request.getIssuer().isWithdrawn()) {
                logger.error("Issuer is revoked.");
                throw new BadRequestException("Issuer is revoked!");
            }

            if (request.getIssuer().getCertificateType() == CertificateType.END) {
                logger.error("End certificate can't generate another certificate.");
                throw new BadRequestException("End certificate can't generate another certificate!");
            }
        }
    }

    @Override
    public CertificateRequestDTO insert(CertificateRequestDTO certificateRequestDTO) {
        Long id = userService.getLoggedUser().getId();
        logger.info(String.format("User with id %d started creating new certificate request.", id));
        User user = getLoggedUser();
        Role role = user.getRoles().get(0);
        CertificateRequest request = new CertificateRequest(certificateRequestDTO);
        Certificate issuer;
        if (request.getCertificateType() == CertificateType.ROOT)
            issuer = null;
        else {
            try {
                issuer = certificateRepository.findBySerialNumber(certificateRequestDTO.getIssuer())
                        .orElseThrow(() -> new NotFoundException("Certificate not found!"));
            }
            catch (NotFoundException e) {
                logger.error("Certificate not found!");
                throw e;
            }
        }
        request.setIssuer(issuer);
        request.setSubject(user);
        request.setRequestType(RequestType.ACTIVE);
        logger.info("Certificate request is active.");

        checkForCreateCertificateExceptions(request);
        if (request.getDate() == null) request.setDate(new Date());

        if (role.getName().equals("ROLE_ADMIN")) {
            logger.info(String.format("Certificate request is accepted by user with id %d.", id));
            request = certificateRequestRepository.save(request);
            acceptRequest(request.getId());
            request.setRequestType(RequestType.ACCEPTED);
        }

        else if (request.getCertificateType() == CertificateType.ROOT) {
            logger.error(String.format("User with id %d can't ask for the root certificate.", id));
            throw new BadRequestException("User can't ask for the root certificate!");
        }

        else if (Objects.equals(request.getIssuer().getSubject().getId(), user.getId())) {
            logger.info("Certificate request is accepted.");
            request = certificateRequestRepository.save(request);
            acceptRequest(request.getId());
            request.setRequestType(RequestType.ACCEPTED);
        }

        request = certificateRequestRepository.save(request);
        logger.info("Certificate request is saved.");
        return new CertificateRequestDTO(request);
    }

}
