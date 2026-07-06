package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.service.RelatorioFinalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/aluno/relatorio")
public class RelatorioFinalController {

    private final RelatorioFinalService relatorioService;

    public RelatorioFinalController(RelatorioFinalService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @PostMapping("/{inscricaoId}")
    public ResponseEntity<Map<String, String>> enviarRelatorio(@PathVariable Long inscricaoId,
                                                               @RequestBody RelatorioFinal relatorio) {
        relatorioService.enviarRelatorio(inscricaoId, relatorio);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Relatório enviado com sucesso"));
    }
}