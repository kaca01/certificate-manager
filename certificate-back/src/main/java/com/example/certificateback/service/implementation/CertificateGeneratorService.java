package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.User;
import com.example.certificateback.domain.ksData.IssuerData;
import com.example.certificateback.domain.ksData.SubjectData;
import com.example.certificateback.service.interfaces.ICertificateGeneratorService;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class CertificateGeneratorService implements ICertificateGeneratorService {

    @Override
    public Certificate generateCertificate(CertificateRequest certificateRequest) {
        SubjectData subjectData = generateSubjectData(certificateRequest);
        IssuerData issuerData = generateIssuerData(certificateRequest);
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");

            // Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider("BC");

            // Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            // Postavljaju se podaci za generisanje sertifikata
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());

            // Generise se sertifikat
            X509CertificateHolder certHolder = certGen.build(contentSigner);
            // Builder generise sertifikat kao objekat klase X509CertificateHolder
            // Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            // Konvertuje objekat u sertifikat
            X509Certificate xCertificate = certConverter.getCertificate(certHolder);
            //we can also check if the digital signature of certificate is valid
            //todo save certificate to db, save to keystore
            //return todo return a converted certificated type (to dto)
        } catch (IllegalArgumentException | IllegalStateException | OperatorCreationException | CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    private SubjectData generateSubjectData(CertificateRequest certificateRequest){
        try {
            KeyPair keyPairSubject = generateKeyPair();
            User user = certificateRequest.getSubject();
            //todo what to do with private key?

            SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
            //todo how to generate validation dates? Does user input that or do we do that through code?
            Date startDate = iso8601Formater.parse("2022-03-01");
            Date endDate = iso8601Formater.parse("2024-03-01");

            String serialNumber = certificateRequest.getSubject().getEmail() + UUID.randomUUID().toString();

            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, user.getUsername());
            builder.addRDN(BCStyle.SURNAME, user.getSurname());
            builder.addRDN(BCStyle.GIVENNAME, user.getName());
            builder.addRDN(BCStyle.E, user.getEmail());
            // UID (USER ID) je ID korisnika
            builder.addRDN(BCStyle.UID, user.getId().toString());

            // Kreiraju se podaci za sertifikat, sto ukljucuje:
            // - javni kljuc koji se vezuje za sertifikat
            // - podatke o vlasniku
            // - serijski broj sertifikata
            // - od kada do kada vazi sertifikat
            return new SubjectData(keyPairSubject.getPublic(), builder.build(), serialNumber, startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private IssuerData generateIssuerData(CertificateRequest certificateRequest){
        User issuer = certificateRequest.getIssuer().getSubject();
        PrivateKey issuerKey = null; //todo treba dobaviti privatni kljuc issuer-a tog sertifikata (digitalni potipis)
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, issuer.getUsername());
        builder.addRDN(BCStyle.SURNAME, issuer.getSurname());
        builder.addRDN(BCStyle.GIVENNAME, issuer.getName());
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
