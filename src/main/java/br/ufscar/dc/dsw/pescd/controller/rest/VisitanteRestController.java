package br.ufscar.dc.dsw.pescd.controller.rest;

import br.ufscar.dc.dsw.pescd.dto.OfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.service.OfertaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * V.01 - Visitante visualiza lista de ofertas (sem login), versão REST.
 * Endpoint público — ver SecurityConfig (permitAll para /api/ofertas).
 */
@RestController
@RequestMapping("/api/ofertas")
public class VisitanteRestController {

    private final OfertaService ofertaService;

    public VisitanteRestController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    // V.01 RN-1: cada oferta exibe número de alunos matriculados
    @GetMapping
    public List<OfertaResponseDTO> listar() {
        List<Oferta> ofertas = ofertaService.listarTodasOrdenadas();
        List<OfertaResponseDTO> resposta = new ArrayList<>();
        for (Oferta oferta : ofertas) {
            resposta.add(new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta)));
        }
        return resposta;
    }
}
