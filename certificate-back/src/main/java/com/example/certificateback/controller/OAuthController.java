package com.example.certificateback.controller;

import com.example.certificateback.service.implementation.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "https://localhost:4200")
public class OAuthController {@Value("${github.clientId}")


    private String clientId;

    @Value("${github.clientSecret}")
    private String clientSecret;


    @PostMapping(value = "/oauth/github")
    public  Mono<String> exchangeCodeForToken(@RequestBody CodeRequest codeRequest) {
        String code = codeRequest.code;
        String redirectUri = "https://localhost:4200/oauth/callback"; // Your callback URL

        // Set up the headers and request entity for the POST request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>("", headers);

        // Build the URL and request parameters for the POST request
        String url = "https://github.com/login/oauth/access_token";
        String params = "client_id={clientId}&client_secret={clientSecret}&code={code}&redirect_uri={redirectUri}";

        clientId = "d9d88e021cc55fe85e59";
        clientSecret = "cb3df518dcc8ab4827586b1f37d416acd720608c";

        // Replace placeholders in the params string with actual values
        params = params.replace("{clientId}", clientId)
                .replace("{clientSecret}", clientSecret)
                .replace("{code}", code)
                .replace("{redirectUri}", redirectUri);

        // Send the POST request to exchange the authorization code for an access token
        WebClient webClient = WebClient.create();

        // Prepare the request body
        String requestBody = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&code=" + code +
                "&redirect_uri=" + redirectUri;

        // Make the POST request
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(String.class);

        // Handle the response and return it to the frontend
//        System.out.println("RESPONSEEEEEE");
//        System.out.println(response.getBody());
//        return ResponseEntity.ok(requestBody);
    }

    static class CodeRequest {
        public String code;

    }

}
