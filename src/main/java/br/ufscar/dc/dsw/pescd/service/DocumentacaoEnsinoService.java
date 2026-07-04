package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.DocumentacaoEnsinoRepository;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentacaoEnsinoService {

    private final DocumentacaoEnsinoRepository documentacaoRepository;
    private final InscricaoOfertaRepository inscricaoRepository;

    public DocumentacaoEnsinoService(DocumentacaoEnsinoRepository documentacaoRepository,
                                     InscricaoOfertaRepository inscricaoRepository) {
        this.documentacaoRepository = documentacaoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    public DocumentacaoEnsino enviarDocumentacao(Long inscricaoId, DocumentacaoEnsino documentacao) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        documentacao.setInscricao(inscricao);
        DocumentacaoEnsino salvo = documentacaoRepository.save(documentacao);

        inscricao.setStatus(StatusAluno.DOCUMENTACAO_ENVIADA);
        inscricaoRepository.save(inscricao);

        return salvo;
    }
}