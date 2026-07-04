package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.service.RelatorioFinalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aluno/relatorio")
public class RelatorioFinalController {

    private final RelatorioFinalService relatorioFinalService;

    public RelatorioFinalController(RelatorioFinalService relatorioFinalService) {
        this.relatorioFinalService = relatorioFinalService;
    }

    @GetMapping("/novo/{inscricaoId}")
    public String mostrarFormulario(@PathVariable Long inscricaoId, Model model) {
        model.addAttribute("relatorio", new RelatorioFinal());
        model.addAttribute("inscricaoId", inscricaoId);
        return "aluno/form-relatorio";
    }

    @PostMapping("/enviar/{inscricaoId}")
    public String enviarRelatorio(@PathVariable Long inscricaoId,
                                  @ModelAttribute RelatorioFinal relatorio,
                                  RedirectAttributes ra) {
        try {
            relatorioFinalService.enviarRelatorio(inscricaoId, relatorio);
            ra.addFlashAttribute("sucesso", "Relatório enviado com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/aluno/ofertas";
    }
}