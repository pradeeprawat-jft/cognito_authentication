package com.psr.awscognitowithoutoauth.controller;

import com.psr.awscognitowithoutoauth.service.CognitoService;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderResponseMetadata;

@Controller
public class MainController {

    @Autowired
    private CognitoService cognitoService;

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, HttpSession session) throws AuthenticationException {
        cognitoService.signup(username, email, password);
        session.setAttribute("username", username);
        return "redirect:/confirm";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("admin/welcome")
    public String adminGreet(Model model) {
        String response = "Welcome admin";
        model.addAttribute("response", response);
        return "welcome";
    }

    @GetMapping("user/welcome")
    public String userGreet(Model model) {
        String response = "Welcome user";
        model.addAttribute("response", response);
        return "welcome";
    }
    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) throws AuthenticationException {
        AuthenticationResultType authResult = cognitoService.login(username, password);
        model.addAttribute("accessToken", authResult.accessToken());
        session.setAttribute("access-token", authResult.accessToken());
        return "redirect:/user/welcome";
    }

    @PostMapping("/confirm")
    public String confirmUser(@RequestParam("confirmCode") String confirmCode, HttpSession session) {
        System.out.println(confirmCode);
        CognitoIdentityProviderResponseMetadata ans = cognitoService.confirmUserSignUp((String) session.getAttribute("username"), confirmCode);
        System.out.println(ans);
        return "redirect:/login";
    }


    @GetMapping("/confirm")
    public String getConfirmPage() {
        return "/confirm";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        cognitoService.logout(String.valueOf(session.getAttribute("access-token")));
        System.out.println(session.getAttribute("access-token"));
        return "redirect:/login";
    }
}