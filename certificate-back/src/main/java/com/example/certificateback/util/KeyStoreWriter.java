package com.example.certificateback.util;

import com.example.certificateback.configuration.KeyStoreConstants;
import com.example.certificateback.repository.IUserRepository;
import jdk.nashorn.internal.runtime.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Service
public class KeyStoreWriter {

    public static void createKeystore() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS", "SUN");
            keyStore.load(null, KeyStoreConstants.KEYSTORE_PASSWORD);
            keyStore.store(Files.newOutputStream(Paths.get(KeyStoreConstants.KEYSTORE_PATH)), KeyStoreConstants.KEYSTORE_PASSWORD);

        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveKeyStore(String fileName, char[] password) {
        KeyStoreReader.load();
        try {
            KeyStoreReader.keyStore.store(Files.newOutputStream(Paths.get(fileName)), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Moze da sacuva novi sertifikat u keystore
     */
    public void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
        KeyStoreReader.load();
        try {
            KeyStoreReader.keyStore.setCertificateEntry(alias + "Cert", certificate);
            KeyStoreReader.keyStore.setKeyEntry(alias + "Key", privateKey, password, new Certificate[]{certificate});
            KeyStoreReader.keyStore.store(Files.newOutputStream(Paths.get(KeyStoreConstants.KEYSTORE_PATH)), KeyStoreConstants.KEYSTORE_PASSWORD);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException | CertificateException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
