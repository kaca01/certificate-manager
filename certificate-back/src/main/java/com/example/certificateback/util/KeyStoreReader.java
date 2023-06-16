package com.example.certificateback.util;

import com.example.certificateback.configuration.KeyStoreConstants;
import com.example.certificateback.domain.ksData.IssuerData;
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

    public static KeyStore keyStore;

    public static void load() {
        try {
            keyStore = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(KeyStoreConstants.KEYSTORE_PATH)));
            keyStore.load(in, KeyStoreConstants.KEYSTORE_PASSWORD);
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException | NoSuchProviderException |
                 CertificateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Zadatak ove funkcije jeste da ucita podatke o izdavaocu i odgovarajuci privatni kljuc.
     * Ovi podaci se mogu iskoristiti da se novi sertifikati izdaju.
    **/
    public static IssuerData readIssuer(String alias, String keyPass) {
        try {
            load();
            Certificate cert = keyStore.getCertificate(alias + "cert");

            // Iscitava se privatni kljuc vezan za javni kljuc koji se nalazi na sertifikatu sa datim aliasom
            PrivateKey privKey = (PrivateKey) keyStore.getKey(alias + "key", keyPass.toCharArray());

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
            load();
            alias = alias + "cert";
            if (keyStore.isCertificateEntry(alias)) {
                Certificate cert = keyStore.getCertificate(alias);
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
            load();
            alias = alias + "key";

            if (keyStore.isKeyEntry(alias)) {
                PrivateKey pk = (PrivateKey) keyStore.getKey(alias, pass.toCharArray());
                return pk;
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
