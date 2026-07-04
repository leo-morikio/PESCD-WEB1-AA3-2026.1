package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.*;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.model.enums.StatusOferta;
import br.ufscar.dc.dsw.pescd.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/professor/responsavel")
public class ProfessorResponsavelController {

    private final OfertaRepository ofertaRepository;

    private final InscricaoOfertaRepository inscricaoOfertaRepository;

    private final UsuarioRepository usuarioRepository;

    private final PlanoTrabalhoRepository planoTrabalhoRepository;

    private final RelatorioFinalRepository relatorioFinalRepository;

    private final DocumentacaoEnsinoRepository documentacaoEnsinoRepository;

    private final LogStatusRepository logStatusRepository;

    public ProfessorResponsavelController(OfertaRepository ofertaRepository, InscricaoOfertaRepository inscricaoOfertaRepository,
                                          UsuarioRepository usuarioRepository, PlanoTrabalhoRepository planoTrabalhoRepository,
                                          RelatorioFinalRepository relatorioFinalRepository, DocumentacaoEnsinoRepository documentacaoEnsinoRepository,
                                          LogStatusRepository logStatusRepository) {

        this.ofertaRepository = ofertaRepository;
        this.inscricaoOfertaRepository = inscricaoOfertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.documentacaoEnsinoRepository = documentacaoEnsinoRepository;
        this.logStatusRepository = logStatusRepository;
    }

    // PR.04 — acompanha ofertas
    @GetMapping("/ofertas")
    public String listarOfertas(Model model) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        List<Oferta> ofertas = ofertaRepository.findByProfessorResponsavel(professor);
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("inscricoesPorOferta",
                ofertas.stream().collect(java.util.stream.Collectors.toMap(
                        Oferta::getId,
                        o -> inscricaoOfertaRepository.findByOferta(o)
                )));
        return "professor/responsavel/ofertas";
    }

    // PR.01 — exibe formulário de conclusão do relatório
    @GetMapping("/concluir-relatorio/{inscricaoId}")
    public String formConcluirRelatorio(@PathVariable Long inscricaoId, Model model) {
        InscricaoOferta inscricao = inscricaoOfertaRepository.findById(inscricaoId).orElseThrow();
        PlanoTrabalho plano = planoTrabalhoRepository.findByInscricao(inscricao).orElse(null);
        RelatorioFinal relatorio = relatorioFinalRepository.findByInscricao(inscricao).orElse(null);
        List<LogStatus> logs = logStatusRepository.findByInscricao(inscricao);
        model.addAttribute("inscricao", inscricao);
        model.addAttribute("plano", plano);
        model.addAttribute("relatorio", relatorio);
        model.addAttribute("logs", logs);
        return "professor/responsavel/concluir-relatorio";
    }

    // PR.01 — processa conclusão do relatório
    @PostMapping("/concluir-relatorio/{inscricaoId}")
    public String concluirRelatorio(@PathVariable Long inscricaoId,
                                    @RequestParam String parecer,
                                    @RequestParam Integer frequencia,
                                    @RequestParam String nota) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = inscricaoOfertaRepository.findById(inscricaoId).orElseThrow();

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoOfertaRepository.save(inscricao);

        logStatusRepository.save(new LogStatus(inscricao, statusAnterior,
                StatusAluno.CONCLUIDO_RESPONSAVEL.name(), professor));

        return "redirect:/professor/responsavel/ofertas";
    }

    // PR.02 — exibe formulário de análise de documentação
    @GetMapping("/analisar-documentacao/{inscricaoId}")
    public String formAnalisarDocumentacao(@PathVariable Long inscricaoId, Model model) {
        InscricaoOferta inscricao = inscricaoOfertaRepository.findById(inscricaoId).orElseThrow();
        DocumentacaoEnsino documentacao = documentacaoEnsinoRepository.findByInscricao(inscricao).orElse(null);
        List<LogStatus> logs = logStatusRepository.findByInscricao(inscricao);
        model.addAttribute("inscricao", inscricao);
        model.addAttribute("documentacao", documentacao);
        model.addAttribute("logs", logs);
        return "professor/responsavel/analisar-documentacao";
    }

    // PR.02 — processa análise de documentação
    @PostMapping("/analisar-documentacao/{inscricaoId}")
    public String analisarDocumentacao(@PathVariable Long inscricaoId,
                                       @RequestParam String parecer,
                                       @RequestParam Integer indicadorFrequencia,
                                       @RequestParam String nota) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = inscricaoOfertaRepository.findById(inscricaoId).orElseThrow();

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoOfertaRepository.save(inscricao);

        logStatusRepository.save(new LogStatus(inscricao, statusAnterior,
                StatusAluno.CONCLUIDO_RESPONSAVEL.name(), professor));

        return "redirect:/professor/responsavel/ofertas";
    }

    // PR.03 — encerra oferta
    @PostMapping("/encerrar-oferta/{ofertaId}")
    public String encerrarOferta(@PathVariable Long ofertaId,
                                 @RequestParam String licoesAprendidas) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        Oferta oferta = ofertaRepository.findById(ofertaId).orElseThrow();

        List<InscricaoOferta> inscricoes = inscricaoOfertaRepository.findByOferta(oferta);
        boolean todosConcluidos = inscricoes.stream()
                .allMatch(i -> i.getStatus() == StatusAluno.CONCLUIDO_RESPONSAVEL);

        if (todosConcluidos) {
            oferta.setStatus(StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO);
            oferta.setEncerradoPor(professor);
            oferta.setEncerradoEm(LocalDateTime.now());
            oferta.setLicoesAprendidas(licoesAprendidas);
            ofertaRepository.save(oferta);
        }

        return "redirect:/professor/responsavel/ofertas";
    }
}