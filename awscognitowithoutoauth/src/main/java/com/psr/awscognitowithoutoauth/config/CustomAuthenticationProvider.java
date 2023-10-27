package com.psr.awscognitowithoutoauth.config;

import com.psr.awscognitowithoutoauth.service.CognitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import java.util.List;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CognitoService cognitoService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        System.out.println(password);
        try {
            AuthenticationResultType authResult = cognitoService.login(username, password);
            System.out.println("Authentication successful for user: " + username);
            // Retrieve user roles from AWS Cognito
//            List<String> userRoles = cognitoService.getUserRoles(username);
//            System.out.println("userRoles: " + userRoles);
            UserDetails userDetails = new CustomUserDetails(username, authResult.accessToken(), authResult.idToken(), authResult.refreshToken());
//            UserDetails userDetails = new CustomUserDetails(username, authResult.accessToken(), authResult.idToken(), authResult.refreshToken(), userRoles);
            System.out.println("userDetails: " + userDetails);
            UsernamePasswordAuthenticationToken data = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            System.out.println("UsernamePasswordAuthenticationToken " + data);
            return data;
        } catch (Exception e) {
            System.out.println("Authentication failed for user: " + username);
            throw new BadCredentialsException("Authentication failed", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
