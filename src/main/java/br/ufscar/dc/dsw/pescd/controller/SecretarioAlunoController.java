package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.repository.DocumentacaoEnsinoRepository;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.LogStatusRepository;
import br.ufscar.dc.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.ufscar.dc.dsw.pescd.repository.RelatorioFinalRepository;
import br.ufscar.dc.dsw.pescd.service.InscricaoOfertaService;
import br.ufscar.dc.dsw.pescd.service.OfertaService;
import br.ufscar.dc.dsw.pescd.service.UsuarioService;
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

    private final OfertaService ofertaService;

    private final UsuarioService usuarioService;

    private final InscricaoOfertaService inscricaoOfertaService;

    private final InscricaoOfertaRepository inscricaoOfertaRepository;

    private final PlanoTrabalhoRepository planoTrabalhoRepository;

    private final RelatorioFinalRepository relatorioFinalRepository;

    private final DocumentacaoEnsinoRepository documentacaoEnsinoRepository;

    private final LogStatusRepository logStatusRepository;

    public SecretarioAlunoController(OfertaService ofertaService, UsuarioService usuarioService, InscricaoOfertaService inscricaoOfertaService,
                                     InscricaoOfertaRepository inscricaoOfertaRepository, PlanoTrabalhoRepository planoTrabalhoRepository,
                                     RelatorioFinalRepository relatorioFinalRepository, DocumentacaoEnsinoRepository documentacaoEnsinoRepository,
                                     LogStatusRepository logStatusRepository) {

        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
        this.inscricaoOfertaService = inscricaoOfertaService;
        this.inscricaoOfertaRepository = inscricaoOfertaRepository;
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.documentacaoEnsinoRepository = documentacaoEnsinoRepository;
        this.logStatusRepository = logStatusRepository;
    }

    // S.03 RN-3 - Detalhes do aluno na inscrição (info + logs)
    @GetMapping("/{inscricaoId}")
    public String detalhesAluno(@PathVariable Long ofertaId,
                                @PathVariable Long inscricaoId,
                                Model model) {
        Oferta oferta = ofertaService.buscarPorId(ofertaId);
        InscricaoOferta inscricao = inscricaoOfertaRepository.findById(inscricaoId)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));
        model.addAttribute("oferta", oferta);
        model.addAttribute("inscricao", inscricao);
        model.addAttribute("plano", planoTrabalhoRepository.findByInscricao(inscricao).orElse(null));
        model.addAttribute("relatorio", relatorioFinalRepository.findByInscricao(inscricao).orElse(null));
        model.addAttribute("documentacao", documentacaoEnsinoRepository.findByInscricao(inscricao).orElse(null));
        model.addAttribute("logs", logStatusRepository.findByInscricao(inscricao));
        return "secretario/alunos/detalhes";
    }

    // S.02 - Formulário para adicionar alunos
    @GetMapping
    public String formAlunos(@PathVariable Long ofertaId, Model model) {
        Oferta oferta = ofertaService.buscarPorId(ofertaId);
        model.addAttribute("oferta", oferta);
        model.addAttribute("inscritos", inscricaoOfertaService.listarPorOferta(oferta));
        model.addAttribute("alunos", usuarioService.listarTodos().stream()
                .filter(u -> u.getPerfil() == Perfil.ALUNO)
                .toList());
        model.addAttribute("professores", usuarioService.listarTodos().stream()
                .filter(u -> u.getPerfil() == Perfil.PROFESSOR)
                .toList());
        return "secretario/alunos/form";
    }

    // S.02 - Inscrição manual (selecionar aluno pelo ID)
    @PostMapping("/inscrever")
    public String inscrever(@PathVariable Long ofertaId,
                            @RequestParam Long alunoId,
                            @RequestParam(required = false) Long supervisorId,
                            RedirectAttributes ra) {
        try {
            Oferta oferta = ofertaService.buscarPorId(ofertaId);
            Usuario aluno = usuarioService.buscarPorId(alunoId);
            Usuario supervisor = supervisorId != null ? usuarioService.buscarPorId(supervisorId) : null;
            inscricaoOfertaService.inscrever(aluno, oferta, supervisor);
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

            List<String> falhas = inscricaoOfertaService.inscreverPorCsv(linhas, oferta);

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
