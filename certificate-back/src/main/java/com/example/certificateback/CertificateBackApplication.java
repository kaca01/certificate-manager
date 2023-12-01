package com.example.certificateback;

import com.example.certificateback.configuration.RecaptchaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

// using 'exclude' only because we don't have database yet (should be deleted in future)
@SpringBootApplication
@EnableConfigurationProperties(RecaptchaProperties.class)
public class CertificateBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertificateBackApplication.class, args);
	}

}
