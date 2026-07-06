package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.service.PlanoTrabalhoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/aluno/plano")
public class PlanoTrabalhoController {

    private final PlanoTrabalhoService planoService;

    public PlanoTrabalhoController(PlanoTrabalhoService planoService) {
        this.planoService = planoService;
    }

    @PostMapping("/{inscricaoId}")
    public ResponseEntity<Map<String, String>> enviarPlano(@PathVariable Long inscricaoId,
                                                           @RequestBody PlanoRequest body) {
        PlanoTrabalho plano = new PlanoTrabalho();
        plano.setCodigoDisciplina(body.codigoDisciplina());
        plano.setNomeDisciplina(body.nomeDisciplina());
        plano.setCurso(body.curso());

        planoService.enviarPlano(inscricaoId, plano, body.supervisorId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensagem", "Plano enviado com sucesso"));
    }

    public record PlanoRequest(String codigoDisciplina, String nomeDisciplina, String curso, Long supervisorId) {}
}