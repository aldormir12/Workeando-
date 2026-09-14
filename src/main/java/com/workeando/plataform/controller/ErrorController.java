package com.workeando.plataform.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/error/403")
    public String accessDenied(Authentication authentication, Model model) {
        String panelUrl = "/"; 

        if (authentication != null && authentication.getAuthorities() != null) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FREELANCER"))) {
                panelUrl = "/free"; 
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLEADOR"))) {
                panelUrl = "/emple"; 
            }
        }

        model.addAttribute("panelUrl", panelUrl);
        return "error403";
    }
}