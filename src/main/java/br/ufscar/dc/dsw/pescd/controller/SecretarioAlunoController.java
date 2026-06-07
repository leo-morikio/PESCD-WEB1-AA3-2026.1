package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.service.InscricaoOfertaService;
import br.ufscar.dc.dsw.pescd.service.OfertaService;
import br.ufscar.dc.dsw.pescd.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

// S.02 - Secretário adiciona alunos à oferta (manual ou CSV)
@Controller
@RequestMapping("/secretario/ofertas/{ofertaId}/alunos")
public class SecretarioAlunoController {

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private InscricaoOfertaService inscricaoService;

    // S.02 - Formulário para adicionar alunos
    @GetMapping
    public String formAlunos(@PathVariable Long ofertaId, Model model) {
        Oferta oferta = ofertaService.buscarPorId(ofertaId);
        model.addAttribute("oferta", oferta);
        model.addAttribute("inscritos", inscricaoService.listarPorOferta(oferta));
        model.addAttribute("alunos", usuarioService.listarTodos().stream()
                .filter(u -> u.getPerfil() == Perfil.ALUNO)
                .toList());
        return "secretario/alunos/form";
    }

    // S.02 - Inscrição manual (selecionar aluno pelo ID)
    @PostMapping("/inscrever")
    public String inscrever(@PathVariable Long ofertaId,
                            @RequestParam Long alunoId,
                            RedirectAttributes ra) {
        try {
            Oferta oferta = ofertaService.buscarPorId(ofertaId);
            Usuario aluno = usuarioService.buscarPorId(alunoId);
            inscricaoService.inscrever(aluno, oferta);
            ra.addFlashAttribute("sucesso", "Aluno inscrito com sucesso.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    // S.02 - Upload CSV formato RA,NOME COMPLETO,EMAIL
    @PostMapping("/upload-csv")
    public String uploadCsv(@PathVariable Long ofertaId,
                             @RequestParam("arquivo") MultipartFile arquivo,
                             RedirectAttributes ra) {
        try {
            Oferta oferta = ofertaService.buscarPorId(ofertaId);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8));

            // Pula o cabeçalho e parseia cada linha em [ra, nome, email]
            List<String[]> linhas = reader.lines()
                    .skip(1)
                    .filter(l -> !l.isBlank())
                    .map(l -> l.split(",", 3))
                    .toList();

            List<String> falhas = inscricaoService.inscreverPorCsv(linhas, oferta);

            if (falhas.isEmpty()) {
                ra.addFlashAttribute("sucesso", linhas.size() + " aluno(s) inscritos com sucesso.");
            } else {
                ra.addFlashAttribute("sucesso", (linhas.size() - falhas.size()) + " inscrito(s).");
                ra.addFlashAttribute("errosCsv", falhas);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("erro", "Erro ao processar arquivo: " + e.getMessage());
        }
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }
}
