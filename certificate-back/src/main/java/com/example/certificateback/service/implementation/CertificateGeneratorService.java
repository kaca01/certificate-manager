package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Certificate;
import com.example.certificateback.domain.CertificateRequest;
import com.example.certificateback.domain.User;
import com.example.certificateback.domain.ksData.IssuerData;
import com.example.certificateback.domain.ksData.SubjectData;
import com.example.certificateback.repository.ICertificateRepository;
import com.example.certificateback.service.interfaces.ICertificateGeneratorService;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class CertificateGeneratorService implements ICertificateGeneratorService {

    @Autowired
    private ICertificateRepository certificateRepository;
    @Autowired
    private KeyStoreWriter keyStoreWriter;

    @Override
    public Certificate generateCertificate(CertificateRequest certificateRequest) {
        SubjectData subjectData = generateSubjectData(certificateRequest);
        //todo if issuer is null
        IssuerData issuerData = KeyStoreReader.readIssuer(Long.toString(certificateRequest.getIssuer().getSerialNumber()), KeyStoreReader.ENTRY_PASSWORD.toCharArray());
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            builder = builder.setProvider("BC");

            // Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    issuerData.getX500name(),
                    new BigInteger(String.valueOf(subjectData.getSerialNumber())),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());
            X509CertificateHolder certHolder = certGen.build(contentSigner);
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            // Konvertuje objekat u sertifikat
            X509Certificate xCertificate = certConverter.getCertificate(certHolder);

            keyStoreWriter.write(
                    xCertificate.getSerialNumber().toString(),
                    subjectData.getPrivateKey(),   //changed from issuer to subject
                    KeyStoreReader.ENTRY_PASSWORD.toCharArray(),
                    xCertificate
            );
            keyStoreWriter.saveKeyStore(KeyStoreReader.KEYSTORE_PATH, KeyStoreReader.KEYSTORE_PASSWORD);
            //save certificate to db
            Certificate certificateEntity = new Certificate(xCertificate, certificateRequest);
            certificateEntity = certificateRepository.save(certificateEntity);
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

            Date startDate = generateStartTime();
            Date endDate = generateEndTime(startDate);

            //todo extract to new function
            Long serialNumber = Long.parseLong(certificateRequest.getSubject().getId().toString() + new Random().nextLong());

            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, user.getUsername());
            builder.addRDN(BCStyle.SURNAME, user.getSurname());
            builder.addRDN(BCStyle.GIVENNAME, user.getName());
            builder.addRDN(BCStyle.E, user.getEmail());
            builder.addRDN(BCStyle.UID, user.getId().toString());

            return new SubjectData(keyPairSubject.getPublic(), keyPairSubject.getPrivate(), builder.build(), serialNumber,
                    startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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

    //todo extract to new file
    public static Date generateStartTime() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return localDateTimeToDate(startOfDay);
    }

    public static Date generateEndTime(Date date){
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endTime = localDateTime.plusYears(1);
        return localDateTimeToDate(endTime);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
