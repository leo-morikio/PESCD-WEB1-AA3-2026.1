package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.RelatorioFinalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RelatorioFinalService {

    @Autowired
    private RelatorioFinalRepository relatorioRepository;

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private LogStatusService logStatusService;

    public void enviarRelatorio(Long inscricaoId, RelatorioFinal relatorio) {
        Optional<InscricaoOferta> opt = inscricaoRepository.findById(inscricaoId);
        if (!opt.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = opt.get();

        if (inscricao.getStatus() != StatusAluno.PLANO_APROVADO) {
            throw new RuntimeException("O plano precisa estar aprovado antes de enviar o relatório.");
        }

        relatorio.setInscricao(inscricao);
        relatorioRepository.save(relatorio);

        StatusAluno anterior = inscricao.getStatus();
        inscricao.setStatus(StatusAluno.RELATORIO_ENVIADO);
        inscricaoRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.RELATORIO_ENVIADO);
    }
}
