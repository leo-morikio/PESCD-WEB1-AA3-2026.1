package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.service.PlanoTrabalhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aluno/plano")
public class PlanoTrabalhoController {

    private final PlanoTrabalhoService planoService;

    public PlanoTrabalhoController(PlanoTrabalhoService planoService) {
        this.planoService = planoService;
    }

    @PostMapping("/{inscricaoId}")
    public ResponseEntity<PlanoTrabalho> enviarPlano(@PathVariable Long inscricaoId,
                                                     @RequestBody PlanoTrabalho plano) {
        PlanoTrabalho salvo = planoService.enviarPlano(inscricaoId, plano);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }
}