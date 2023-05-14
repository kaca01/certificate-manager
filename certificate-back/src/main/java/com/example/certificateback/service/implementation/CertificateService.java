package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.AllDTO;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.ICertificateRepository;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    IUserRepository userRepository;

    @Autowired
    ICertificateRepository certificateRepository;

    @Override
    public List<CertificateDTO> getAllCertificates() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = userRepository.findByEmail(authentication.getName()).orElse(null);

        if(loggedUser != null) {
            List<Certificate> certificates = certificateRepository.findAll();
            List<CertificateDTO> certificatesDTO = new ArrayList<>();
            for (Certificate c : certificates){
                certificatesDTO.add(new CertificateDTO(c.getValidTo(), c.getValidFrom(), c.getSubject(), c.getCertificateType(),
                                    c.getSerialNumber()));
            }
            return certificatesDTO;
        }
        return null;
    }

    @Override
    public Boolean checkingValidation(String serialNumber) {
        Certificate certificate = certificateRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new NotFoundException("Certificate with that serial number does not exist"));

        if(certificate != null)
            return certificate.isValid() && !certificate.isWithdrawn();

        return false;
    }

    @Override
    public Boolean isValidByCopy(String path) {
        Boolean isValid = false;
        try {
            path = "C:\\Users\\User\\Desktop\\-1347043084476417129cert.crt";
            FileInputStream inputStream = new FileInputStream(path);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);
            inputStream.close();

            System.out.println("SERIAL NUMBERRRRR");
            System.out.println(certificate.getSerialNumber());
            isValid = checkingValidation(certificate.getSerialNumber().toString());
        } catch (CertificateException | IOException e) {
            throw new NotFoundException("File not found!");
        }
        return isValid;
    }

    @Override
    public AllDTO<CertificateDTO> getIssuers() {
        List<CertificateDTO> certificates = getAllCertificates();
        certificates.removeIf(certificate -> Objects.equals(certificate.getCertificateType(), "END"));
        AllDTO<CertificateDTO> certificatesDTO = new AllDTO<>(certificates);
        return certificatesDTO;
    }
}
