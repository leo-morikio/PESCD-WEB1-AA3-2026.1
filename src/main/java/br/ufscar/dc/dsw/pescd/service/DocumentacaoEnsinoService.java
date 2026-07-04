package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.DocumentacaoEnsinoRepository;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentacaoEnsinoService {

    private final DocumentacaoEnsinoRepository documentacaoEnsinoRepository;

    private final InscricaoOfertaRepository inscricaoOfertaRepository;

    private final LogStatusService logStatusService;

    public DocumentacaoEnsinoService(DocumentacaoEnsinoRepository documentacaoEnsinoRepository, InscricaoOfertaRepository inscricaoOfertaRepository,
                                     LogStatusService logStatusService) {

        this.documentacaoEnsinoRepository = documentacaoEnsinoRepository;
        this.inscricaoOfertaRepository = inscricaoOfertaRepository;
        this.logStatusService = logStatusService;
    }

    public void enviarDocumentacao(Long inscricaoId, DocumentacaoEnsino documentacao) {
        InscricaoOferta inscricao = inscricaoOfertaRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        StatusAluno anterior = inscricao.getStatus();
        documentacao.setInscricao(inscricao);
        documentacaoEnsinoRepository.save(documentacao);

        inscricao.setStatus(StatusAluno.DOCUMENTACAO_ENVIADA);
        inscricaoOfertaRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.DOCUMENTACAO_ENVIADA);
    }
}