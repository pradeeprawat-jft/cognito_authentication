package com.psr.awscognitowithoutoauth.controller;

import com.psr.awscognitowithoutoauth.service.CognitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;

import java.util.Arrays;

@Controller
public class MainController {

    @Autowired
    private CognitoService cognitoService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,@RequestParam String email, @RequestParam String password) {
        cognitoService.signup(username,email, password);
        return "redirect:/welcome";
    }
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/welcome")
    public String dashboard() {
        return "welcome";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password) {
        AuthenticationResultType authResult = cognitoService.login(username, password);
        System.out.println("authResult: " + authResult);
       System.out.println(Arrays.toString(authResult.accessToken().split(" ")));
        return "redirect:/welcome";
    }
}
