package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import br.ufscar.dc.dsw.pescd.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.RelatorioFinalRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RelatorioFinalService {

    private final RelatorioFinalRepository relatorioFinalRepository;

    private final InscricaoOfertaRepository inscricaoOfertaRepository;

    private final LogStatusService logStatusService;

    public RelatorioFinalService(RelatorioFinalRepository relatorioFinalRepository, InscricaoOfertaRepository inscricaoOfertaRepository, LogStatusService logStatusService) {
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.inscricaoOfertaRepository = inscricaoOfertaRepository;
        this.logStatusService = logStatusService;
    }

    // AL.04 - aluno envia relatório final (PC-4: plano precisa estar aprovado)
    public void enviarRelatorio(Long inscricaoId, RelatorioFinal relatorio, Usuario aluno) {
        Optional<InscricaoOferta> opt = inscricaoOfertaRepository.findById(inscricaoId);
        if (!opt.isPresent()) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        InscricaoOferta inscricao = opt.get();

        if (!inscricao.getAluno().getId().equals(aluno.getId())) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }

        if (inscricao.getStatus() != StatusAluno.PLANO_APROVADO) {
            throw new NegocioException("O plano precisa estar aprovado antes de enviar o relatório.");
        }

        relatorio.setInscricao(inscricao);
        relatorioFinalRepository.save(relatorio);

        StatusAluno anterior = inscricao.getStatus();
        inscricao.setStatus(StatusAluno.RELATORIO_ENVIADO);
        inscricaoOfertaRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.RELATORIO_ENVIADO);
    }
}
