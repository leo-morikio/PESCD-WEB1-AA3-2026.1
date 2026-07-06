package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.service.ArquivoStorageService;
import br.ufscar.dc.dsw.pescd.service.RelatorioFinalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/aluno/relatorio")
public class RelatorioFinalController {

    private final RelatorioFinalService relatorioService;
    private final ArquivoStorageService arquivoStorageService;

    public RelatorioFinalController(RelatorioFinalService relatorioService,
                                    ArquivoStorageService arquivoStorageService) {
        this.relatorioService = relatorioService;
        this.arquivoStorageService = arquivoStorageService;
    }

    @PostMapping(value = "/{inscricaoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> enviarRelatorio(
            @PathVariable Long inscricaoId,
            @RequestParam Integer indicadorFrequencia,
            @RequestParam("arquivo") MultipartFile arquivo) {

        String caminho = arquivoStorageService.salvarPdf(arquivo);

        RelatorioFinal relatorio = new RelatorioFinal();
        relatorio.setIndicadorFrequencia(indicadorFrequencia);
        relatorio.setCaminhoArquivo(caminho);

        relatorioService.enviarRelatorio(inscricaoId, relatorio);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Relatório enviado com sucesso"));
    }
}