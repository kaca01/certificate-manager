package com.example.certificateback.util;

import com.example.certificateback.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Service
public class KeyStoreWriter {

    public void saveKeyStore(String fileName, char[] password) {
        KeyStore keyStore = KeyStoreReader.load();
        try {
            keyStore.store(Files.newOutputStream(Paths.get(fileName)), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Moze da sacuva novi sertifikat u keystore
     */
    public void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
        KeyStore keyStore = KeyStoreReader.load();
        try {
            keyStore.setKeyEntry(alias, privateKey, password, new Certificate[]{certificate});
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
