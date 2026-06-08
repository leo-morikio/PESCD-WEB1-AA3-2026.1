package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    public void salvar(Usuario usuario) {
        // AD.01 RN-4: email único
        Usuario existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente != null && !existente.getId().equals(usuario.getId())) {
            throw new RuntimeException("E-mail já cadastrado: " + usuario.getEmail());
        }

        Usuario existenteNome = usuarioRepository.findByNomeUsuario(usuario.getNomeUsuario());
        if (existenteNome != null && !existenteNome.getId().equals(usuario.getId())) {
            throw new RuntimeException("Nome de usuário já cadastrado: " + usuario.getNomeUsuario());
        }

        if (usuario.getId() != null && (usuario.getSenha() == null || usuario.getSenha().isBlank())) {
            // Edição sem nova senha: mantém a senha atual do banco
            Usuario salvo = usuarioRepository.findById(usuario.getId()).get();
            usuario.setSenha(salvo.getSenha());
        } else {
            // Novo usuário ou edição com nova senha: encripta
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        usuarioRepository.save(usuario);
    }

    public void excluir(Long id, Long idLogado) {
        // AD.01 RN-3: não pode remover o próprio usuário
        if (id.equals(idLogado)) {
            throw new RuntimeException("Não é possível remover seu próprio usuário.");
        }
        usuarioRepository.deleteById(id);
    }
}
