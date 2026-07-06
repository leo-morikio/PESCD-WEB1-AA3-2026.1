package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.service.ArquivoStorageService;
import br.ufscar.dc.dsw.pescd.service.DocumentacaoEnsinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/aluno/documentacao")
public class DocumentacaoEnsinoController {

    private final DocumentacaoEnsinoService documentacaoService;
    private final ArquivoStorageService arquivoStorageService;

    public DocumentacaoEnsinoController(DocumentacaoEnsinoService documentacaoService,
                                        ArquivoStorageService arquivoStorageService) {
        this.documentacaoService = documentacaoService;
        this.arquivoStorageService = arquivoStorageService;
    }

    @PostMapping(value = "/{inscricaoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> enviarDocumentacao(
            @PathVariable Long inscricaoId,
            @RequestParam String nomeInstituicao,
            @RequestParam String nomeDisciplina,
            @RequestParam String curso,
            @RequestParam Integer cargaHoraria,
            @RequestParam("arquivo") MultipartFile arquivo) {

        String caminho = arquivoStorageService.salvarPdf(arquivo);

        DocumentacaoEnsino documentacao = new DocumentacaoEnsino();
        documentacao.setNomeInstituicao(nomeInstituicao);
        documentacao.setNomeDisciplina(nomeDisciplina);
        documentacao.setCurso(curso);
        documentacao.setCargaHoraria(cargaHoraria);
        documentacao.setCaminhoArquivo(caminho);

        documentacaoService.enviarDocumentacao(inscricaoId, documentacao);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Documentação enviada com sucesso"));
    }
}