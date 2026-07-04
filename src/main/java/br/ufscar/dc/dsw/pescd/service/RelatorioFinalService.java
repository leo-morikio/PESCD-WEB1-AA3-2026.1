package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.RelatorioFinalRepository;
import org.springframework.stereotype.Service;

@Service
public class RelatorioFinalService {

    private final RelatorioFinalRepository relatorioRepository;
    private final InscricaoOfertaRepository inscricaoRepository;

    public RelatorioFinalService(RelatorioFinalRepository relatorioRepository,
                                 InscricaoOfertaRepository inscricaoRepository) {
        this.relatorioRepository = relatorioRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public RelatorioFinal enviarRelatorio(Long inscricaoId, RelatorioFinal relatorio) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        if (inscricao.getStatus() != StatusAluno.PLANO_APROVADO) {
            throw new RuntimeException("O plano precisa estar aprovado antes de enviar o relatório.");
        }

        relatorio.setInscricao(inscricao);
        RelatorioFinal salvo = relatorioRepository.save(relatorio);

        inscricao.setStatus(StatusAluno.RELATORIO_ENVIADO);
        inscricaoRepository.save(inscricao);

        return salvo;
    }
}