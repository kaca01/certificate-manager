package com.example.certificateback.service.implementation;

import com.example.certificateback.configuration.KeyStoreConstants;
import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.enumeration.RequestType;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.exception.WrongUserException;
import com.example.certificateback.repository.ICertificateRepository;
import com.example.certificateback.repository.ICertificateRequestRepository;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.ICertificateService;
import com.example.certificateback.service.interfaces.IUserService;
import com.example.certificateback.util.KeyStoreReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.security.cert.*;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    ICertificateRepository certificateRepository;

    @Autowired
    ICertificateRequestRepository certificateRequestRepository;

    @Autowired
    IUserService userService;

    private static final Logger logger = LoggerFactory.getLogger(CertificateService.class);


    private User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) throw new NotFoundException("User not found!");
        //List<CertificateRequest> certificateRequests = certificateRequestRepository.findBySubjectId(user.getId());
        return user;
    }

    @Override
    public List<CertificateDTO> getAllCertificates() {
        Long id = userService.getLoggedUser().getId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(loggedUser != null) {
            List<Certificate> certificates = certificateRepository.findAll();
            List<CertificateDTO> certificatesDTO = new ArrayList<>();
            for (Certificate c : certificates){
                certificatesDTO.add(new CertificateDTO(c.getValidTo(), c.getValidFrom(), c.getSubject(), c.getCertificateType(),
                                    c.getSerialNumber()));
            }
            logger.info(String.format("Returned all certificates to user with id %d.", id));
            return certificatesDTO;
        }
        logger.info(String.format("Returned null while getting all certificates to a user with id %d.", id));
        return null;
    }

    @Override
    public Boolean checkingValidation(String serialNumber) {
        Long id = userService.getLoggedUser().getId();
        try {
            Certificate certificate = certificateRepository.findBySerialNumber(serialNumber)
                    .orElseThrow(() -> new NotFoundException("Certificate with that serial number does not exist"));

            if (certificate != null) {
                logger.info(String.format("Validation by serial number result returned to a user with id %d.", id));
                return certificate.isValid() && !certificate.isWithdrawn();
            }

            logger.info(String.format("Validation by serial number result returned to a user with id %d.", id));
            return false;
        }
        catch (NotFoundException e) {
            logger.error("Certificate with chosen serial number does not exist.");
            throw e;
        }
    }

    @Override
    public Boolean isValidByCopy(byte[] file) {
        Long id = userService.getLoggedUser().getId();

        logger.info(String.format("User with id %d is checking validation by uploaded certificate copy.", id));
        Boolean isValid;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(file);
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);

            certificate.checkValidity();
            isValid = checkingValidation(certificate.getSerialNumber().toString());
            inputStream.close();
        } catch (CertificateException | IOException e) {
            logger.error(String.format("Error occurred while user with id %d was checking validation of uploaded certificate copy.", id));
            throw new BadRequestException("Error occurred, please check file type and try again!");
        }
        logger.info(String.format("Validation by copy result returned to a user with id %d.", id));
        return isValid;
    }

    @Override
    public AllDTO<CertificateDTO> getIssuers() {
        Long id = userService.getLoggedUser().getId();
        List<CertificateDTO> certificates = getAllCertificates();
        certificates.removeIf(certificate -> Objects.equals(certificate.getCertificateType(), "END"));
        AllDTO<CertificateDTO> certificatesDTO = new AllDTO<>(certificates);
        logger.info(String.format("Issuers returned to a user with id %d.", id));
        return certificatesDTO;
    }

    @Override
    public CertificateDTO invalidate(String serialNumber, String withdrawnReason) {
        Long id = userService.getLoggedUser().getId();

        try {
            logger.info(String.format("User with id %d is trying to revoke a certificate.", id));
            Certificate certificate = certificateRepository.findBySerialNumber(serialNumber)
                    .orElseThrow(() -> new NotFoundException("Certificate with that serial number does not exist"));

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User loggedUser = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (loggedUser != certificate.getSubject())
                throw new BadRequestException("Only certificate owner can revoke the certificate.");

            if (certificate.isWithdrawn()) throw new BadRequestException(("Certificate is not valid!"));

            certificate.setWithdrawn(true);
            certificate.setWithdrawnReason(withdrawnReason);
            certificateRepository.save(certificate);
            revokeChildren(certificate);
            certificateRepository.flush();
            certificateRequestRepository.flush();
            logger.info(String.format("Certificate revoked by user with id %d.", id));
            return new CertificateDTO(certificate);
        }
        catch (NotFoundException | BadRequestException e) {
            if (e.getClass().getName().equals("NotFoundException")) logger.error("Certificate does not exist.");
            else logger.error(String.format("Bad request. User with id %d is not owner or the certificate is not valid.", id));
            throw e;
        }
    }

    private void revokeChildren(Certificate certificate) {
        // TODO : should I put here logs, is that too much infos?
        if (!certificate.isWithdrawn()) {
            certificate.setWithdrawn(true);
            certificate.setWithdrawnReason("Issuer is revoked. This is refused by the system.");
            certificateRepository.save(certificate);
        }

        // the following lines are refusing all active cert requests with issuer that is revoked
        List<CertificateRequest> activeRequests = certificateRequestRepository.
                findByIssuerAndRequestType(certificate, RequestType.ACTIVE);

        for (CertificateRequest request : activeRequests) {
            request.setRequestType(RequestType.REFUSED);
            request.setRefusalReason("Issuer is revoked. This is refused by the system.");
            certificateRequestRepository.save(request);
        }

        List<Certificate> childrenCertificates = certificateRepository.findByIssuerSerialNumber(certificate.getSerialNumber());
        for (Certificate child : childrenCertificates) {
            revokeChildren(child);
        }
    }

    @Override
    public ByteArrayResource downloadCertificate(String serialNumber) {
        Long id = userService.getLoggedUser().getId();

        try {
            logger.info(String.format("User with id %d is trying to download certificate.", id));
            certificateRepository.findBySerialNumber(serialNumber).orElseThrow(()
                    -> new NotFoundException("Certificate with that serial number does not exist"));

            java.security.cert.Certificate cert = KeyStoreReader.readCertificate(serialNumber);
            assert cert != null;
            logger.info(String.format("Returned certificate data for download to a user with id %d.", id));
            return new ByteArrayResource(cert.getEncoded());
        } catch (NotFoundException | CertificateEncodingException e) {
            if (e.getClass().getName().equals("NotFoundException")) {
                logger.error("Certificate with chosen serial number does not exist.");
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public ByteArrayResource downloadPrivateKey(String serialNumber) {
        Long id = userService.getLoggedUser().getId();

        try {
            logger.info(String.format("User with id %d is trying to download private key.", id));
            Certificate cert = certificateRepository.findBySerialNumber(serialNumber).orElseThrow(()
                    -> new NotFoundException("Certificate with that serial number does not exist"));

            if (!getLoggedUser().getEmail().equals(cert.getSubject().getEmail())
                    && getLoggedUser().getRoles().get(0).getName().equals("ROLE_USER"))
                throw new WrongUserException("You are not owner of this certificate!");

            PrivateKey pk = KeyStoreReader.readPrivateKey(serialNumber, KeyStoreConstants.ENTRY_PASSWORD);

            assert pk != null;
            logger.info(String.format("Returned private key data for download to a user with id %d.", id));
            return new ByteArrayResource(pk.getEncoded());
        }
        catch (NotFoundException | WrongUserException e) {
            if (e.getClass().getName().equals("NotFoundException"))
                logger.error("Certificate with chosen serial number does not exist.");
            else logger.error(String.format("User with id %d is not the owner of the certificate.", id));
            throw e;
        }
    }
}
