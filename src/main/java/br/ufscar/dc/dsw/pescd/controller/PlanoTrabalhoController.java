package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.service.ArquivoStorageService;
import br.ufscar.dc.dsw.pescd.service.PlanoTrabalhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/aluno/plano")
public class PlanoTrabalhoController {

    private final PlanoTrabalhoService planoService;
    private final ArquivoStorageService arquivoStorageService;

    public PlanoTrabalhoController(PlanoTrabalhoService planoService,
                                   ArquivoStorageService arquivoStorageService) {
        this.planoService = planoService;
        this.arquivoStorageService = arquivoStorageService;
    }

    @PostMapping(value = "/{inscricaoId}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> enviarPlano(
            @PathVariable Long inscricaoId,
            @RequestParam String codigoDisciplina,
            @RequestParam String nomeDisciplina,
            @RequestParam String curso,
            @RequestParam Long supervisorId,
            @RequestParam("arquivo") MultipartFile arquivo) {

        String caminho = arquivoStorageService.salvarPdf(arquivo);

        PlanoTrabalho plano = new PlanoTrabalho();
        plano.setCodigoDisciplina(codigoDisciplina);
        plano.setNomeDisciplina(nomeDisciplina);
        plano.setCurso(curso);
        plano.setCaminhoArquivo(caminho);

        planoService.enviarPlano(inscricaoId, plano, supervisorId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Plano enviado com sucesso"));
    }
}