package br.ufscar.dc.dsw.pescd.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// U.01 - Autenticação e redirecionamento por perfil
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // U.01 RN-2: redireciona para área do perfil após login
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth == null) return "redirect:/login";

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/usuarios";
        }
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SECRETARIO"))) {
            return "redirect:/secretario/ofertas";
        }
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROFESSOR"))) {
            return "redirect:/professor/ofertas";
        }
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ALUNO"))) {
            return "redirect:/aluno/ofertas";
        }
        return "redirect:/";
    }
}
