package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InscricaoOfertaService {

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

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
        inscricaoRepository.findByAlunoAndOferta(aluno, oferta).ifPresent(i -> {
            throw new RuntimeException("Aluno já está inscrito nesta oferta.");
        });
        InscricaoOferta inscricao = new InscricaoOferta();
        inscricao.setAluno(aluno);
        inscricao.setOferta(oferta);
        inscricao.setStatus(StatusAluno.NAO_ENVIADO);
        inscricaoRepository.save(inscricao);
    }

    /**
     * S.02 CSV - Inscreve múltiplos alunos por e-mail.
     * Retorna lista de e-mails que falharam (não encontrados ou já inscritos).
     */
    public List<String> inscreverPorEmails(List<String> emails, Oferta oferta,
                                           br.ufscar.dc.dsw.pescd.repository.UsuarioRepository usuarioRepository) {
        List<String> falhas = new java.util.ArrayList<>();
        for (String email : emails) {
            String e = email.trim();
            if (e.isBlank()) continue;
            Usuario aluno = usuarioRepository.findByEmail(e);
            if (aluno == null) {
                falhas.add(e + " (não encontrado)");
                continue;
            }
            try {
                inscrever(aluno, oferta);
            } catch (RuntimeException ex) {
                falhas.add(e + " (" + ex.getMessage() + ")");
            }
        }
        return falhas;
    }

    public void excluir(Long id) {
        inscricaoRepository.deleteById(id);
    }
}
