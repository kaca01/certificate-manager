package com.example.certificateback.configuration;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "google.recaptcha")
public class RecaptchaProperties {

    private String secret;

    private String url;

    private double scoreThreshold;
}
