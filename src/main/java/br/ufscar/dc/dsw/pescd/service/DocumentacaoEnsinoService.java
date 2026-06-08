package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.DocumentacaoEnsinoRepository;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentacaoEnsinoService {

    @Autowired
    private DocumentacaoEnsinoRepository documentacaoRepository;

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private LogStatusService logStatusService;

    public void enviarDocumentacao(Long inscricaoId, DocumentacaoEnsino documentacao) {
        Optional<InscricaoOferta> opt = inscricaoRepository.findById(inscricaoId);
        if (!opt.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = opt.get();

        StatusAluno anterior = inscricao.getStatus();
        documentacao.setInscricao(inscricao);
        documentacaoRepository.save(documentacao);

        inscricao.setStatus(StatusAluno.DOCUMENTACAO_ENVIADA);
        inscricaoRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.DOCUMENTACAO_ENVIADA);
    }
}
