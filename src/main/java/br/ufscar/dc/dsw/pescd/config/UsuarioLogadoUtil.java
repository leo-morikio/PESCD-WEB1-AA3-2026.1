package br.ufscar.dc.dsw.pescd.config;

import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilitário para obter o Usuario JPA a partir do contexto de segurança.
 * U.01 - substitui os hardcodes temporários nos controllers.
 */
public class UsuarioLogadoUtil {

    public static Usuario getUsuarioLogado(UsuarioRepository usuarioRepository) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Nenhum usuário autenticado.");
        }
        String nomeUsuario = auth.getName();
        Usuario usuario = usuarioRepository.findByNomeUsuario(nomeUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuário autenticado não encontrado: " + nomeUsuario);
        }
        return usuario;
    }
}
