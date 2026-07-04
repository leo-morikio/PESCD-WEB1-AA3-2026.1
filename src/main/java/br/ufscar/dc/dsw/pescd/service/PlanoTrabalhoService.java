package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.PlanoTrabalhoRepository;
import org.springframework.stereotype.Service;

@Service
public class PlanoTrabalhoService {

    private final PlanoTrabalhoRepository planoRepository;
    private final InscricaoOfertaRepository inscricaoRepository;

    public PlanoTrabalhoService(PlanoTrabalhoRepository planoRepository,
                                InscricaoOfertaRepository inscricaoRepository) {
        this.planoRepository = planoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public PlanoTrabalho enviarPlano(Long inscricaoId, PlanoTrabalho plano) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        plano.setInscricao(inscricao);
        PlanoTrabalho salvo = planoRepository.save(plano);

        inscricao.setStatus(StatusAluno.PLANO_ENVIADO);
        inscricaoRepository.save(inscricao);

        return salvo;
    }
}