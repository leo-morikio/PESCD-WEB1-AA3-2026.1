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
import java.util.Optional;

@Controller
@RequestMapping("/professor/supervisor")
public class ProfessorSupervisorController {

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanoTrabalhoRepository planoRepository;

    @Autowired
    private RelatorioFinalRepository relatorioRepository;

    @Autowired
    private LogStatusRepository logStatusRepository;

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
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

        PlanoTrabalho plano = null;
        Optional<PlanoTrabalho> optPlano = planoRepository.findByInscricao(inscricao);
        if (optPlano.isPresent()) {
            plano = optPlano.get();
        }

        model.addAttribute("inscricao", inscricao);
        model.addAttribute("plano", plano);
        return "professor/supervisor/aprovar-plano";
    }

    // PS.02 — processa aprovação do plano
    @PostMapping("/aprovar-plano/{inscricaoId}")
    public String aprovarPlano(@PathVariable Long inscricaoId,
                               @RequestParam String parecer) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

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
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

        PlanoTrabalho plano = null;
        Optional<PlanoTrabalho> optPlano = planoRepository.findByInscricao(inscricao);
        if (optPlano.isPresent()) {
            plano = optPlano.get();
        }

        RelatorioFinal relatorio = null;
        Optional<RelatorioFinal> optRelatorio = relatorioRepository.findByInscricao(inscricao);
        if (optRelatorio.isPresent()) {
            relatorio = optRelatorio.get();
        }

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
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.RELATORIO_APROVADO_SUPERVISOR);
        inscricaoRepository.save(inscricao);

        logStatusRepository.save(new LogStatus(inscricao, statusAnterior,
                StatusAluno.RELATORIO_APROVADO_SUPERVISOR.name(), professor));

        return "redirect:/professor/supervisor/ofertas";
    }
}
