package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InscricaoOfertaService {

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LogStatusService logStatusService;

    public List<InscricaoOferta> listarPorOferta(Oferta oferta) {
        return inscricaoRepository.findByOferta(oferta);
    }

    public List<InscricaoOferta> listarPorAluno(Usuario aluno) {
        return inscricaoRepository.findByAluno(aluno);
    }

    /**
     * S.02 - Inscreve aluno na oferta.
     * RN-1: aluno não pode ser inscrito duas vezes na mesma oferta.
     */
    public void inscrever(Usuario aluno, Oferta oferta) {
        inscrever(aluno, oferta, null);
    }

    public void inscrever(Usuario aluno, Oferta oferta, Usuario supervisor) {
        inscricaoRepository.findByAlunoAndOferta(aluno, oferta).ifPresent(i -> {
            throw new RuntimeException("Aluno já está inscrito nesta oferta.");
        });
        InscricaoOferta inscricao = new InscricaoOferta();
        inscricao.setAluno(aluno);
        inscricao.setOferta(oferta);
        inscricao.setProfessorSupervisor(supervisor);
        inscricao.setStatus(StatusAluno.NAO_ENVIADO);
        inscricaoRepository.save(inscricao);

        logStatusService.registrar(inscricao, null, StatusAluno.NAO_ENVIADO);
    }

    /**
     * S.02 CSV - Formato: RA,NOME COMPLETO,EMAIL (com cabeçalho).
     * Se o aluno não existir no banco, cria com nomeUsuario=email, senha=RA.
     * Retorna lista de linhas que falharam.
     */
    public List<String> inscreverPorCsv(List<String[]> linhas, Oferta oferta) {
        List<String> falhas = new ArrayList<>();
        for (String[] campos : linhas) {
            if (campos.length < 3) {
                falhas.add(campos[0] + " (linha inválida)");
                continue;
            }
            String ra = campos[0].trim();
            String nomeCompleto = campos[1].trim();
            String email = campos[2].trim();

            if (email.isBlank()) continue;

            Usuario aluno = usuarioRepository.findByEmail(email);
            if (aluno == null) {
                // Cria o aluno: nomeUsuario=email, senha=RA
                aluno = new Usuario();
                aluno.setNomeCompleto(nomeCompleto);
                aluno.setEmail(email);
                aluno.setNomeUsuario(email);
                aluno.setSenha(passwordEncoder.encode(ra));
                aluno.setPerfil(Perfil.ALUNO);
                usuarioRepository.save(aluno);
            }

            try {
                inscrever(aluno, oferta);
            } catch (RuntimeException ex) {
                falhas.add(email + " (" + ex.getMessage() + ")");
            }
        }
        return falhas;
    }

    public void excluir(Long id) {
        inscricaoRepository.deleteById(id);
    }
}
