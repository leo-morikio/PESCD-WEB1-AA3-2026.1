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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private PlanoTrabalhoRepository planoRepository;

    @Autowired
    private RelatorioFinalRepository relatorioRepository;

    @Autowired
    private DocumentacaoEnsinoRepository documentacaoRepository;

    @Autowired
    private LogStatusRepository logStatusRepository;

    // S.03 RN-3 - Detalhes do aluno na inscrição (info + logs)
    @GetMapping("/{inscricaoId}")
    public String detalhesAluno(@PathVariable Long ofertaId,
                                @PathVariable Long inscricaoId,
                                Model model) {
        Oferta oferta = ofertaService.buscarPorId(ofertaId);
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

        model.addAttribute("oferta", oferta);
        model.addAttribute("inscricao", inscricao);

        Optional optPlano = planoRepository.findByInscricao(inscricao);
        if (optPlano.isPresent()) {
            model.addAttribute("plano", optPlano.get());
        } else {
            model.addAttribute("plano", null);
        }

        Optional optRelatorio = relatorioRepository.findByInscricao(inscricao);
        if (optRelatorio.isPresent()) {
            model.addAttribute("relatorio", optRelatorio.get());
        } else {
            model.addAttribute("relatorio", null);
        }

        Optional optDocumentacao = documentacaoRepository.findByInscricao(inscricao);
        if (optDocumentacao.isPresent()) {
            model.addAttribute("documentacao", optDocumentacao.get());
        } else {
            model.addAttribute("documentacao", null);
        }

        model.addAttribute("logs", logStatusRepository.findByInscricao(inscricao));
        return "secretario/alunos/detalhes";
    }

    // S.02 - Formulário para adicionar alunos
    @GetMapping
    public String formAlunos(@PathVariable Long ofertaId, Model model) {
        Oferta oferta = ofertaService.buscarPorId(ofertaId);
        List<Usuario> todosUsuarios = usuarioService.listarTodos();
        List<Usuario> alunos = new ArrayList<>();
        List<Usuario> professores = new ArrayList<>();
        for (Usuario u : todosUsuarios) {
            if (u.getPerfil() == Perfil.ALUNO) {
                alunos.add(u);
            } else if (u.getPerfil() == Perfil.PROFESSOR) {
                professores.add(u);
            }
        }
        model.addAttribute("oferta", oferta);
        model.addAttribute("inscritos", inscricaoService.listarPorOferta(oferta));
        model.addAttribute("alunos", alunos);
        model.addAttribute("professores", professores);
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
            Usuario supervisor = null;
            if (supervisorId != null) {
                supervisor = usuarioService.buscarPorId(supervisorId);
            }
            inscricaoService.inscrever(aluno, oferta, supervisor);
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

            List<String[]> linhas = new ArrayList<>();
            reader.readLine(); // pula o cabeçalho
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.isBlank()) {
                    linhas.add(linha.split(",", 3));
                }
            }

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
