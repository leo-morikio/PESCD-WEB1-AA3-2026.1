package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import br.ufscar.dc.dsw.pescd.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.OfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final InscricaoOfertaRepository inscricaoOfertaRepository;
    private final OfertaRepository ofertaRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                          InscricaoOfertaRepository inscricaoOfertaRepository, OfertaRepository ofertaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.inscricaoOfertaRepository = inscricaoOfertaRepository;
        this.ofertaRepository = ofertaRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (!opt.isPresent()) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + id);
        }
        return opt.get();
    }

    public void salvar(Usuario usuario) {
        // AD.01 RN-4: email único
        Usuario existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente != null && !existente.getId().equals(usuario.getId())) {
            throw new NegocioException("E-mail já cadastrado: " + usuario.getEmail());
        }

        Usuario existenteNome = usuarioRepository.findByNomeUsuario(usuario.getNomeUsuario());
        if (existenteNome != null && !existenteNome.getId().equals(usuario.getId())) {
            throw new NegocioException("Nome de usuário já cadastrado: " + usuario.getNomeUsuario());
        }

        if (usuario.getId() != null && (usuario.getSenha() == null || usuario.getSenha().isBlank())) {
            // Edição sem nova senha: mantém a senha atual do banco
            Optional<Usuario> optSalvo = usuarioRepository.findById(usuario.getId());
            if (!optSalvo.isPresent()) {
                throw new RecursoNaoEncontradoException("Usuário não encontrado: " + usuario.getId());
            }
            usuario.setSenha(optSalvo.get().getSenha());
        } else {
            // Novo usuário ou edição com nova senha: encripta
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long id, Long idLogado) {
        // AD.01 RN-3: não pode remover o próprio usuário
        if (id.equals(idLogado)) {
            throw new NegocioException("Não é possível remover seu próprio usuário.");
        }
        Optional<Usuario> optUsuario = usuarioRepository.findById(id);
        if (!optUsuario.isPresent()) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado: " + id);
        }
        Usuario usuario = optUsuario.get();

        // AD.01 RN-5: exclusão em cascata conforme o perfil do usuário
        if (usuario.getPerfil() == Perfil.ALUNO) {
            // Remove as inscrições do aluno antes de remover o usuário
            inscricaoOfertaRepository.deleteByAlunoId(id);
        }
        if (usuario.getPerfil() == Perfil.SECRETARIO) {
            // Desvincula as ofertas criadas por esse secretário (não apaga as ofertas)
            List<Oferta> ofertas = ofertaRepository.findByCriadoPorId(id);
            for (Oferta oferta : ofertas) {
                oferta.setCriadoPor(null);
                ofertaRepository.save(oferta);
            }
        }

        usuarioRepository.delete(usuario);
    }
}
