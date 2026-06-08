package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.service.PlanoTrabalhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aluno/plano")
public class PlanoTrabalhoController {

    @Autowired
    private PlanoTrabalhoService planoService;

    @GetMapping("/novo/{inscricaoId}")
    public String mostrarFormulario(@PathVariable Long inscricaoId, Model model) {
        model.addAttribute("plano", new PlanoTrabalho());
        model.addAttribute("inscricaoId", inscricaoId);
        return "aluno/form-plano";
    }

    @PostMapping("/enviar/{inscricaoId}")
    public String enviarPlano(@PathVariable Long inscricaoId,
                              @ModelAttribute PlanoTrabalho plano) {
        planoService.enviarPlano(inscricaoId, plano);
        return "redirect:/aluno/ofertas";
    }
}