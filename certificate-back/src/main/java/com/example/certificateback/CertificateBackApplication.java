package com.example.certificateback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// using 'exclude' only because we don't have database yet (should be deleted in future)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class CertificateBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertificateBackApplication.class, args);
	}

}
