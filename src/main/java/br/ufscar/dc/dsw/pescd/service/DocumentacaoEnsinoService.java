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

    @Autowired
    private DocumentacaoEnsinoRepository documentacaoRepository;

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    public void enviarDocumentacao(Long inscricaoId, DocumentacaoEnsino documentacao) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        documentacao.setInscricao(inscricao);
        documentacaoRepository.save(documentacao);

        inscricao.setStatus(StatusAluno.DOCUMENTACAO_ENVIADA);
        inscricaoRepository.save(inscricao);
    }
}