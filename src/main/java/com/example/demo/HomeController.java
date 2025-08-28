package com.example.demo;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/secure1")
    public String securePage(@AuthenticationPrincipal Saml2AuthenticatedPrincipal principal, Model model) {
        System.out.println("Model --22222222222->"+principal.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"));
        model.addAttribute("name", principal.getAttribute("http://schemas.microsoft.com/identity/claims/displayname"));
        model.addAttribute("email", principal.getAttribute("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"));
        System.out.println("Model --->"+model);
        return "secure";
    }
}
