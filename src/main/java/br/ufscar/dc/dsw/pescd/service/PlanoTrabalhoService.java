package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.PlanoTrabalhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanoTrabalhoService {

    @Autowired
    private PlanoTrabalhoRepository planoRepository;

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private LogStatusService logStatusService;

    public void enviarPlano(Long inscricaoId, PlanoTrabalho plano) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        plano.setInscricao(inscricao);
        planoRepository.save(plano);

        StatusAluno anterior = inscricao.getStatus();
        inscricao.setStatus(StatusAluno.PLANO_ENVIADO);
        inscricaoRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.PLANO_ENVIADO);
    }
}