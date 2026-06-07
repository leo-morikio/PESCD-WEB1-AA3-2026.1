package br.ufscar.dc.dsw.pescd.config;

import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

// U.01 - Configuração de autenticação e autorização
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return nomeUsuario -> {
            Usuario usuario = usuarioRepository.findByNomeUsuario(nomeUsuario);
            if (usuario == null) {
                throw new UsernameNotFoundException("Usuário não encontrado: " + nomeUsuario);
            }
            return new User(
                    usuario.getNomeUsuario(),
                    usuario.getSenha(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name()))
            );
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // U.01 RN-1: página inicial pública (visitante)
                .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                // Rotas por perfil
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/secretario/**").hasRole("SECRETARIO")
                .requestMatchers("/professor/**").hasRole("PROFESSOR")
                .requestMatchers("/aluno/**").hasRole("ALUNO")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?erro=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
