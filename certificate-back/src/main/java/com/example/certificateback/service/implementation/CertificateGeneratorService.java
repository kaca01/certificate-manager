package com.example.certificateback.service.implementation;

import com.example.certificateback.configuration.KeyStoreConstants;
import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.User;
import com.example.certificateback.domain.ksData.IssuerData;
import com.example.certificateback.domain.ksData.SubjectData;
import com.example.certificateback.dto.CertificateDTO;
import com.example.certificateback.repository.ICertificateRepository;
import com.example.certificateback.repository.ICertificateRequestRepository;
import com.example.certificateback.service.interfaces.ICertificateGeneratorService;
import com.example.certificateback.util.DateUtil;
import com.example.certificateback.util.KeyStoreReader;
import com.example.certificateback.util.KeyStoreWriter;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class CertificateGeneratorService implements ICertificateGeneratorService {

    @Autowired
    private ICertificateRepository certificateRepository;

    @Autowired
    private ICertificateRequestRepository certificateRequestRepository;
    @Autowired
    private KeyStoreWriter keyStoreWriter;

    @Override
    public CertificateDTO createCertificate(CertificateRequest certificateRequest) {
        SubjectData subjectData = generateSubjectData(certificateRequest);
        IssuerData issuerData;
        if (certificateRequest.getIssuer() != null)
            // if it isn't root certificate
            issuerData = generateIssuerData(certificateRequest);
        else {
            // if it is root certificate
            issuerData = new IssuerData(subjectData.getX500name(), subjectData.getPrivateKey());
        }

        try {
            X509Certificate xCertificate = generateCertificateData(issuerData, subjectData);

            // save certificate to keystore
            keyStoreWriter.write(
                    xCertificate.getSerialNumber().toString(),
                    subjectData.getPrivateKey(),   //changed from issuer to subject
                    KeyStoreConstants.KEYSTORE_PASSWORD,
                    xCertificate
            );
            //keyStoreWriter.saveKeyStore(KeyStoreConstants.KEYSTORE_PATH, KeyStoreConstants.KEYSTORE_PASSWORD);

            //save certificate to db
            Certificate certificateEntity = new Certificate(xCertificate, certificateRequest);
            certificateEntity = certificateRepository.save(certificateEntity);

            return new CertificateDTO(certificateEntity);

        } catch (IllegalArgumentException | IllegalStateException | OperatorCreationException | CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    private X509Certificate generateCertificateData(IssuerData issuerData, SubjectData subjectData) throws CertificateException, OperatorCreationException {
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder = builder.setProvider("BC");

        // Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
        ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                issuerData.getX500name(),
                new BigInteger(subjectData.getSerialNumber()),
                subjectData.getStartDate(),
                subjectData.getEndDate(),
                subjectData.getX500name(),
                subjectData.getPublicKey());
        X509CertificateHolder certHolder = certGen.build(contentSigner);
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        certConverter = certConverter.setProvider("BC");

        // Konvertuje objekat u sertifikat
        return certConverter.getCertificate(certHolder);
    }

    private String generateAlias(User user){
        return String.valueOf(new Random().nextLong());
    }

    private SubjectData generateSubjectData(CertificateRequest certificateRequest){
        KeyPair keyPairSubject = generateKeyPair();
        User user = certificateRequest.getSubject();

        DateUtil dateUtil = new DateUtil();
        Date startDate = dateUtil.generateStartTime();
        Date endDate = dateUtil.generateEndTime(startDate);
        String serialNumber = generateAlias(certificateRequest.getSubject());

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getUsername());
        builder.addRDN(BCStyle.SURNAME, user.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, user.getName());
        builder.addRDN(BCStyle.E, user.getEmail());
        builder.addRDN(BCStyle.UID, user.getId().toString());

        return new SubjectData(keyPairSubject.getPublic(), keyPairSubject.getPrivate(), builder.build(), serialNumber,
                startDate, endDate);
    }

    private IssuerData generateIssuerData(CertificateRequest certificateRequest){
        User issuer = certificateRepository.findById(certificateRequest.getIssuer().getId()).get().getSubject();
        PrivateKey issuerKey = KeyStoreReader.readPrivateKey(certificateRequest.getIssuer().getSerialNumber(), KeyStoreConstants.ENTRY_PASSWORD);
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, issuer.getUsername());
        builder.addRDN(BCStyle.SURNAME, issuer.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, issuer.getName());
        builder.addRDN(BCStyle.E, issuer.getEmail());
        builder.addRDN(BCStyle.UID, String.valueOf(issuer.getId()));

        return new IssuerData(builder.build(), issuerKey);
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

}
