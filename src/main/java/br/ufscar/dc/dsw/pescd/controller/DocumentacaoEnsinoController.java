package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.ArquivoStorageService;
import br.ufscar.dc.dsw.pescd.service.DocumentacaoEnsinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/aluno/documentacao")
public class DocumentacaoEnsinoController {

    private final DocumentacaoEnsinoService documentacaoService;
    private final ArquivoStorageService arquivoStorageService;
    private final UsuarioRepository usuarioRepository;

    public DocumentacaoEnsinoController(DocumentacaoEnsinoService documentacaoService,
                                        ArquivoStorageService arquivoStorageService,
                                        UsuarioRepository usuarioRepository) {
        this.documentacaoService = documentacaoService;
        this.arquivoStorageService = arquivoStorageService;
        this.usuarioRepository = usuarioRepository;
    }

    // AL.03 - aluno envia documentação de ensino (PC-4: plano precisa estar aprovado)
    @PostMapping(value = "/{inscricaoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> enviarDocumentacao(
            @PathVariable Long inscricaoId,
            @RequestParam String nomeInstituicao,
            @RequestParam String nomeDisciplina,
            @RequestParam String curso,
            @RequestParam Integer cargaHoraria,
            @RequestParam("arquivo") MultipartFile arquivo) {

        Usuario aluno = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        String caminho = arquivoStorageService.salvarPdf(arquivo);

        DocumentacaoEnsino documentacao = new DocumentacaoEnsino();
        documentacao.setNomeInstituicao(nomeInstituicao);
        documentacao.setNomeDisciplina(nomeDisciplina);
        documentacao.setCurso(curso);
        documentacao.setCargaHoraria(cargaHoraria);
        documentacao.setCaminhoArquivo(caminho);

        documentacaoService.enviarDocumentacao(inscricaoId, documentacao, aluno);

        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Documentação enviada com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }
}
