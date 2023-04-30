package com.example.certificateback.util;

import com.example.certificateback.configuration.KeyStoreConstants;
import com.example.certificateback.domain.ksData.IssuerData;
import com.sun.deploy.association.utility.AppConstants;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

@Service
public class KeyStoreReader {

    public static KeyStore load() {
        KeyStore keyStore = null;

        try {
            keyStore = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try(InputStream keyStoreData = Files.newInputStream(Paths.get(KeyStoreConstants.KEYSTORE_PATH))){
            keyStore.load(keyStoreData, KeyStoreConstants.KEYSTORE_PASSWORD);
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        }

        return keyStore;
    }

    /**
     * Zadatak ove funkcije jeste da ucita podatke o izdavaocu i odgovarajuci privatni kljuc.
     * Ovi podaci se mogu iskoristiti da se novi sertifikati izdaju.
    **/
    public static IssuerData readIssuer(String alias, char[] keyPass) {
        try {
            KeyStore keyStore = load();

            Certificate cert = keyStore.getCertificate(alias);

            // Iscitava se privatni kljuc vezan za javni kljuc koji se nalazi na sertifikatu sa datim aliasom
            PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPass);

            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new IssuerData(issuerName, privKey);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException
                 | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ucitava sertifikat is KS fajla
     */
    public static Certificate readCertificate(String alias) {
        try {
            KeyStore ks = load();

            if (ks.isKeyEntry(alias)) {
                Certificate cert = ks.getCertificate(alias);
                return cert;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ucitava privatni kljuc is KS fajla
     */
    public static PrivateKey readPrivateKey(String alias, String pass) {
        try {
            // kreiramo instancu KeyStore
            KeyStore ks = load();

            if (ks.isKeyEntry(alias)) {
                PrivateKey pk = (PrivateKey) ks.getKey(alias, pass.toCharArray());
                return pk;
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
