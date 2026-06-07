package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
