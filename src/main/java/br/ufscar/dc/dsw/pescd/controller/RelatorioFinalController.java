package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.service.RelatorioFinalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aluno/relatorio")
public class RelatorioFinalController {

    private final RelatorioFinalService relatorioService;

    public RelatorioFinalController(RelatorioFinalService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @PostMapping("/{inscricaoId}")
    public ResponseEntity<RelatorioFinal> enviarRelatorio(@PathVariable Long inscricaoId,
                                                          @RequestBody RelatorioFinal relatorio) {
        RelatorioFinal salvo = relatorioService.enviarRelatorio(inscricaoId, relatorio);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
}