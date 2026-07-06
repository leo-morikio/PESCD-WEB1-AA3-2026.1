package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.LogStatus;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.model.enums.StatusOferta;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.LogStatusRepository;
import br.ufscar.dc.dsw.pescd.repository.OfertaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfessorResponsavelService {

    private final InscricaoOfertaRepository inscricaoRepository;
    private final OfertaRepository ofertaRepository;
    private final LogStatusRepository logStatusRepository;

    public ProfessorResponsavelService(InscricaoOfertaRepository inscricaoRepository,
                                       OfertaRepository ofertaRepository,
                                       LogStatusRepository logStatusRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.ofertaRepository = ofertaRepository;
        this.logStatusRepository = logStatusRepository;
    }

    // PR.01 - conclui o relatório do aluno
    public InscricaoOferta concluirRelatorio(Long inscricaoId, Usuario professor) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        if (inscricao.getStatus() != StatusAluno.RELATORIO_APROVADO_SUPERVISOR) {
            throw new RuntimeException("O relatório precisa estar aprovado pelo supervisor antes de concluir.");
        }

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoRepository.save(inscricao);
        registrarLog(inscricao, statusAnterior, StatusAluno.CONCLUIDO_RESPONSAVEL.name(), professor);
        return inscricao;
    }

    // PR.02 - analisa documentação e conclui
    public InscricaoOferta analisarDocumentacao(Long inscricaoId, Usuario professor) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        if (inscricao.getStatus() != StatusAluno.DOCUMENTACAO_ENVIADA) {
            throw new RuntimeException("Só é possível analisar uma documentação que foi enviada.");
        }

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoRepository.save(inscricao);
        registrarLog(inscricao, statusAnterior, StatusAluno.CONCLUIDO_RESPONSAVEL.name(), professor);
        return inscricao;
    }

    // PR.03 - encerra oferta (só se todas inscrições concluídas)
    public Oferta encerrarOferta(Long ofertaId, String licoesAprendidas, Usuario professor) {
        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Oferta não encontrada"));

        List<InscricaoOferta> inscricoes = inscricaoRepository.findByOferta(oferta);
        boolean todosConcluidos = inscricoes.stream()
                .allMatch(i -> i.getStatus() == StatusAluno.CONCLUIDO_RESPONSAVEL);

        if (!todosConcluidos) {
            throw new RuntimeException("Nem todos os alunos concluíram. Não é possível encerrar.");
        }

        oferta.setStatus(StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO);
        oferta.setEncerradoPor(professor);
        oferta.setEncerradoEm(LocalDateTime.now());
        oferta.setLicoesAprendidas(licoesAprendidas);
        ofertaRepository.save(oferta);
        return oferta;
    }

    // lista ofertas do professor responsável
    public List<Oferta> listarOfertas(Usuario professor) {
        return ofertaRepository.findByProfessorResponsavel(professor);
    }

    private void registrarLog(InscricaoOferta inscricao, String anterior, String novo, Usuario professor) {
        logStatusRepository.save(new LogStatus(inscricao, anterior, novo, professor));
    }
}