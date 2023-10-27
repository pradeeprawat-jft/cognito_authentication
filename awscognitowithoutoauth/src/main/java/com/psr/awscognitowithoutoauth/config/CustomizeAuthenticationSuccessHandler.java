package com.psr.awscognitowithoutoauth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomizeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        for (GrantedAuthority auth : authentication.getAuthorities()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails customUser) {
                // Handle the case where the principal is of type CustomUserDetails
                System.out.println("Custom User: " + customUser.getUsername());
                if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                    System.out.println(customUser.getUsername() + " Is Admin!");
//                    response.sendRedirect("/admin/welcome");
                } else if ("ROLE_USER".equals(auth.getAuthority())) {
                    System.out.println(customUser.getUsername() + " Is User!");
                    response.sendRedirect("/user/welcome");
                }
            } else if (principal instanceof DefaultOidcUser defaultOidcUser) {
                Map<String, Object> userAttributes = defaultOidcUser.getAttributes();
                System.out.println("OIDC User Attributes: " + userAttributes);

                if ("ROLE_ADMIN".equals(auth.getAuthority())) {
                    System.out.println(userAttributes.get("cognito:username") + " Is Admin!");
                    response.sendRedirect("/admin/welcome");
                } else if ("ROLE_USER".equals(auth.getAuthority())) {
                    System.out.println(userAttributes.get("cognito:username") + " Is User!");
                    response.sendRedirect("/user/welcome");
                }
            }
        }
    }
}
