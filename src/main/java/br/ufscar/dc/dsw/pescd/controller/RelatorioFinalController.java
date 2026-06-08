package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.service.RelatorioFinalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aluno/relatorio")
public class RelatorioFinalController {

    @Autowired
    private RelatorioFinalService relatorioService;

    @GetMapping("/novo/{inscricaoId}")
    public String mostrarFormulario(@PathVariable Long inscricaoId, Model model) {
        model.addAttribute("relatorio", new RelatorioFinal());
        model.addAttribute("inscricaoId", inscricaoId);
        return "aluno/form-relatorio";
    }

    @PostMapping("/enviar/{inscricaoId}")
    public String enviarRelatorio(@PathVariable Long inscricaoId,
                                  @ModelAttribute RelatorioFinal relatorio) {
        relatorioService.enviarRelatorio(inscricaoId, relatorio);
        return "redirect:/aluno/ofertas";
    }
}