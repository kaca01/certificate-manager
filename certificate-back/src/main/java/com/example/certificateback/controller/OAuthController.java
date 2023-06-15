package com.example.certificateback.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OAuthController {@Value("${github.clientId}")
private String clientId;

    @Value("${github.clientSecret}")
    private String clientSecret;

    @PostMapping("/oauth/github")
    public ResponseEntity<?> exchangeCodeForToken(@RequestBody CodeRequest codeRequest) {
        String code = codeRequest.code;
        String redirectUri = "http://localhost:4200/callback"; // Your callback URL

        // Exchange the authorization code for an access token
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://github.com/login/oauth/access_token";
        String params = String.format("client_id=%s&client_secret=%s&code=%s&redirect_uri=%s",
                clientId, clientSecret, code, redirectUri);
        ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class, params);

        // Handle the response and return it to the frontend
        return ResponseEntity.ok(response.getBody());
    }

    static class CodeRequest {
        public String code;

    }

}
