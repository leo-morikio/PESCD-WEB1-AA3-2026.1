package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.service.DocumentacaoEnsinoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aluno/documentacao")
public class DocumentacaoEnsinoController {

    @Autowired
    private DocumentacaoEnsinoService documentacaoService;

    @GetMapping("/novo/{inscricaoId}")
    public String mostrarFormulario(@PathVariable Long inscricaoId, Model model) {
        model.addAttribute("documentacao", new DocumentacaoEnsino());
        model.addAttribute("inscricaoId", inscricaoId);
        return "aluno/form-documentacao";
    }

    @PostMapping("/enviar/{inscricaoId}")
    public String enviarDocumentacao(@PathVariable Long inscricaoId,
                                     @ModelAttribute DocumentacaoEnsino documentacao) {
        documentacaoService.enviarDocumentacao(inscricaoId, documentacao);
        return "redirect:/aluno/ofertas";
    }
}