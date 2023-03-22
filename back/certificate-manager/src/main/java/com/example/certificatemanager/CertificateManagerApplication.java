package com.example.certificatemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// uncomment next line to start the app without database configuration
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@SpringBootApplication
public class CertificateManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertificateManagerApplication.class, args);
	}

}
