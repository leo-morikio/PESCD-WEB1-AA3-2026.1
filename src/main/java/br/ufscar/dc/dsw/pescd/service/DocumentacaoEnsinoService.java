package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import br.ufscar.dc.dsw.pescd.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.DocumentacaoEnsinoRepository;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    // AL.03 - aluno envia documentação de ensino já ministrado, para receber os créditos
    // sem realizar o estágio. É uma via ALTERNATIVA ao caminho do estágio (AL.02/AL.04):
    // só pode ser usada se nenhum plano de trabalho tiver sido enviado para esta inscrição.
    public void enviarDocumentacao(Long inscricaoId, DocumentacaoEnsino documentacao, Usuario aluno) {
        Optional<InscricaoOferta> opt = inscricaoOfertaRepository.findById(inscricaoId);
        if (!opt.isPresent()) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        InscricaoOferta inscricao = opt.get();

        if (!inscricao.getAluno().getId().equals(aluno.getId())) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }

        if (inscricao.getStatus() != StatusAluno.NAO_ENVIADO) {
            throw new NegocioException("Não é possível enviar documentação: já existe um plano de trabalho (estágio) em andamento para esta inscrição, ou a documentação já foi enviada.");
        }

        StatusAluno anterior = inscricao.getStatus();
        documentacao.setInscricao(inscricao);
        documentacaoEnsinoRepository.save(documentacao);

        inscricao.setStatus(StatusAluno.DOCUMENTACAO_ENVIADA);
        inscricaoOfertaRepository.save(inscricao);

        logStatusService.registrar(inscricao, anterior, StatusAluno.DOCUMENTACAO_ENVIADA);
    }
}
