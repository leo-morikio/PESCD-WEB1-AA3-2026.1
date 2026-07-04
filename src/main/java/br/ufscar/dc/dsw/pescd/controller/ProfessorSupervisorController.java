package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.LogStatus;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.LogStatusRepository;
import br.ufscar.dc.dsw.pescd.repository.PlanoTrabalhoRepository;
import br.ufscar.dc.dsw.pescd.repository.RelatorioFinalRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/professor/supervisor")
public class ProfessorSupervisorController {

    private final InscricaoOfertaRepository inscricaoRepository;

    private final UsuarioRepository usuarioRepository;

    private final PlanoTrabalhoRepository planoTrabalhoRepository;

    private final RelatorioFinalRepository relatorioFinalRepository;

    private final LogStatusRepository logStatusRepository;

    public ProfessorSupervisorController(InscricaoOfertaRepository inscricaoRepository, UsuarioRepository usuarioRepository,
                                         PlanoTrabalhoRepository planoTrabalhoRepository, RelatorioFinalRepository relatorioFinalRepository,
                                         LogStatusRepository logStatusRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.logStatusRepository = logStatusRepository;
    }

    // PS.01
    @GetMapping("/ofertas")
    public String listarOfertas(Model model) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        List<InscricaoOferta> inscricoes = inscricaoRepository.findByProfessorSupervisor(professor);
        model.addAttribute("inscricoes", inscricoes);
        return "professor/supervisor/ofertas";
    }

    // PS.02 — exibe formulário de aprovação do plano
    @GetMapping("/aprovar-plano/{inscricaoId}")
    public String formAprovarPlano(@PathVariable Long inscricaoId, Model model) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId).orElseThrow();
        PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao).orElse(null);
        model.addAttribute("inscricao", inscricao);
        model.addAttribute("plano", plano);
        return "professor/supervisor/aprovar-plano";
    }

    // PS.02 — processa aprovação do plano
    @PostMapping("/aprovar-plano/{inscricaoId}")
    public String aprovarPlano(@PathVariable Long inscricaoId,
                               @RequestParam String parecer) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId).orElseThrow();

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.PLANO_APROVADO);
        inscricaoRepository.save(inscricao);

        logStatusRepository.save(new LogStatus(inscricao, statusAnterior,
                StatusAluno.PLANO_APROVADO.name(), professor));

        return "redirect:/professor/supervisor/ofertas";
    }

    // PS.03 — exibe formulário de aprovação do relatório
    @GetMapping("/aprovar-relatorio/{inscricaoId}")
    public String formAprovarRelatorio(@PathVariable Long inscricaoId, Model model) {
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId).orElseThrow();
        PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao).orElse(null);
        RelatorioFinal relatorio = relatorioFinalRepository.findByInscricao(inscricao).orElse(null);
        model.addAttribute("inscricao", inscricao);
        model.addAttribute("plano", plano);
        model.addAttribute("relatorio", relatorio);
        return "professor/supervisor/aprovar-relatorio";
    }

    // PS.03 — processa aprovação do relatório
    @PostMapping("/aprovar-relatorio/{inscricaoId}")
    public String aprovarRelatorio(@PathVariable Long inscricaoId,
                                   @RequestParam String parecer,
                                   @RequestParam Integer indicadorFrequencia,
                                   @RequestParam String sugestaoNota) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = inscricaoRepository.findById(inscricaoId).orElseThrow();

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.RELATORIO_APROVADO_SUPERVISOR);
        inscricaoRepository.save(inscricao);

        logStatusRepository.save(new LogStatus(inscricao, statusAnterior,
                StatusAluno.RELATORIO_APROVADO_SUPERVISOR.name(), professor));

        return "redirect:/professor/supervisor/ofertas";
    }
}