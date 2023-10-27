package com.psr.awscognitowithoutoauth.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String accessToken;
    private final String idToken;
    private final String refreshToken;

    private List<GrantedAuthority> authorityList;


//    public CustomUserDetails(String username, String accessToken, String idToken, String refreshToken , List<String> roles) {
//        this.username = username;
//        this.accessToken = accessToken;
//        this.idToken = idToken;
//        this.refreshToken = refreshToken;
//
//        this.authorityList = roles.stream()
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList());
//
//    }

    public CustomUserDetails(String username, String accessToken, String idToken, String refreshToken) {
        this.username = username;
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorityList = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorityList;
    }
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement account expiration logic if needed.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement account locking logic if needed.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement credentials expiration logic if needed.
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement account enable/disable logic if needed.
    }

}
