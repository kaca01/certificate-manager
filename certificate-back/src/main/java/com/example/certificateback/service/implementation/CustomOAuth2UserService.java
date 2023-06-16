package com.example.certificateback.service.implementation;
import com.example.certificateback.domain.User;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    // ...
    @Autowired
    private IUserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // Retrieve the access token
        String accessToken = userRequest.getAccessToken().getTokenValue();

        // Verify the access token with GitHub's API
        // You can use your own implementation to validate the token

        // Retrieve the user's email from the OAuth2 user details
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");

        // Create a user session with the authenticated user
        User userDetails = userService.findByEmail(oauth2User.getName());

        System.out.print("LOAD USERRRRRR");

        return new DefaultOAuth2User(userDetails.getAuthorities(), attributes, "login");
    }
}