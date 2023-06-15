package com.example.certificateback.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin(origins = "https://localhost:4200")
public class OAuthController {@Value("${github.clientId}")

    private String clientId;

    @Value("${github.clientSecret}")
    private String clientSecret;

    @PostMapping(value = "/oauth/github", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exchangeCodeForToken(@RequestBody CodeRequest codeRequest) {
        String code = codeRequest.code;
        String redirectUri = "https://localhost:4200/oauth/callback"; // Your callback URL

        // Exchange the authorization code for an access token
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://github.com/login/oauth/access_token";
        String params = "client_id=Iv1.17fb5574a2cb3f37&client_secret=8ad063a44d6c6b093cbbbeb5fa1ea6eb3ee03678" +
                "&code=" + code + "&redirect_uri=" + redirectUri;
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class, params);

        // Handle the response and return it to the frontend
        return ResponseEntity.ok(response.getBody());
    }

    static class CodeRequest {
        public String code;

    }

}
