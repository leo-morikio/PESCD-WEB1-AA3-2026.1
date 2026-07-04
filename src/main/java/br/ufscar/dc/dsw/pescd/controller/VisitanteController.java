package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// V.01 - Visitante visualiza lista de ofertas (sem login)
@Controller
public class VisitanteController {

    private final OfertaService ofertaService;

    public VisitanteController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Oferta> ofertas = ofertaService.listarTodasOrdenadas();
        // V.01 RN-1: cada oferta exibe número de alunos matriculados
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("ofertaService", ofertaService);
        return "visitante/lista-ofertas";
    }
}
