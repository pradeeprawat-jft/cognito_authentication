//package com.psr.awscognitowithoutoauth.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(request -> request.requestMatchers("/","/login","/register","/confirm").permitAll()
//                        .requestMatchers("/admin/*").hasRole("ADMIN")
//                        .requestMatchers("/user/*").hasAnyRole("ADMIN", "USER").anyRequest().authenticated())
//                .formLogin(formLogin ->
//                        formLogin
//                                .loginPage("/login") // Specify the custom login page URL
//                                .successForwardUrl("/welcome") // Redirect after successful login
//                                .permitAll()
//                );
//        return http.build();
//    }
//}