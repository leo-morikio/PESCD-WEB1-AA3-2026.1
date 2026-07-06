package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.service.DocumentacaoEnsinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/aluno/documentacao")
public class DocumentacaoEnsinoController {

    private final DocumentacaoEnsinoService documentacaoService;

    public DocumentacaoEnsinoController(DocumentacaoEnsinoService documentacaoService) {
        this.documentacaoService = documentacaoService;
    }

    @PostMapping("/{inscricaoId}")
    public ResponseEntity<Map<String, String>> enviarDocumentacao(@PathVariable Long inscricaoId,
                                                                  @RequestBody DocumentacaoEnsino documentacao) {
        documentacaoService.enviarDocumentacao(inscricaoId, documentacao);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Documentação enviada com sucesso"));
    }
}