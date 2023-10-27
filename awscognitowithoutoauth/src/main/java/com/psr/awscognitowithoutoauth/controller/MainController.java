package com.psr.awscognitowithoutoauth.controller;
import com.psr.awscognitowithoutoauth.service.CognitoService;
import jakarta.servlet.http.HttpSession;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderResponseMetadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping("/admin/welcome")
    public String adminGreet(Model model) {
        String response = "Welcome admin";
        model.addAttribute("response", response);
        return "welcome";
    }

    @GetMapping("/user/welcome")
    public String userGreet(Model model) {
        String response = "Welcome user";
        model.addAttribute("response", response);
        return "welcome";
    }


    @GetMapping("/user/form")
    public String form() {
        String response = "Welcome user";
        return "form";
    }

    @PostMapping("/doLogin")
    public String loginUser(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) throws AuthenticationException {
        AuthenticationResultType authResult = cognitoService.login(username, password);
        model.addAttribute("accessToken", authResult.accessToken());
        session.setAttribute("access-token", authResult.accessToken());
        System.out.println("came here once ----------------------------------------------------------------");
        return "redirect:/user/welcome";
    }

    @PostMapping("/confirm")
    public String confirmUser(@RequestParam("confirmCode") String confirmCode, HttpSession session) {
        System.out.println(confirmCode);
        CognitoIdentityProviderResponseMetadata ans = cognitoService.confirmUserSignUp((String) session.getAttribute("username"), confirmCode);
        System.out.println(ans);
        return "redirect:/login";
    }


    @GetMapping("/api/me")
    public ResponseEntity<Map<String, Object>> getUserInfo(Authentication authentication) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", authentication.getName());

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        userInfo.put("roles", roles);

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/confirm")
    public String getConfirmPage() {
        return "/confirm";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpSession session) {
        cognitoService.logout(String.valueOf(session.getAttribute("access-token")));
        System.out.println(session.getAttribute("access-token"));
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }
}