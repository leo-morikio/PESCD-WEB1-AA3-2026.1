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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/professor/responsavel")
public class ProfessorResponsavelController {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanoTrabalhoRepository planoRepository;

    @Autowired
    private RelatorioFinalRepository relatorioRepository;

    @Autowired
    private DocumentacaoEnsinoRepository documentacaoRepository;

    @Autowired
    private LogStatusRepository logStatusRepository;

    // PR.04 — acompanha ofertas
    @GetMapping("/ofertas")
    public String listarOfertas(Model model) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        List<Oferta> ofertas = ofertaRepository.findByProfessorResponsavel(professor);
        Map<Long, List<InscricaoOferta>> inscricoesPorOferta = new HashMap<>();
        for (Oferta o : ofertas) {
            inscricoesPorOferta.put(o.getId(), inscricaoRepository.findByOferta(o));
        }
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("inscricoesPorOferta", inscricoesPorOferta);
        return "professor/responsavel/ofertas";
    }

    // PR.01 — exibe formulário de conclusão do relatório
    @GetMapping("/concluir-relatorio/{inscricaoId}")
    public String formConcluirRelatorio(@PathVariable Long inscricaoId, Model model) {
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
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoRepository.save(inscricao);

        logStatusRepository.save(new LogStatus(inscricao, statusAnterior,
                StatusAluno.CONCLUIDO_RESPONSAVEL.name(), professor));

        return "redirect:/professor/responsavel/ofertas";
    }

    // PR.02 — exibe formulário de análise de documentação
    @GetMapping("/analisar-documentacao/{inscricaoId}")
    public String formAnalisarDocumentacao(@PathVariable Long inscricaoId, Model model) {
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

        DocumentacaoEnsino documentacao = null;
        Optional<DocumentacaoEnsino> optDocumentacao = documentacaoRepository.findByInscricao(inscricao);
        if (optDocumentacao.isPresent()) {
            documentacao = optDocumentacao.get();
        }

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
        Optional<InscricaoOferta> optInscricao = inscricaoRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RuntimeException("Inscrição não encontrada");
        }
        InscricaoOferta inscricao = optInscricao.get();

        String statusAnterior = inscricao.getStatus().name();
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoRepository.save(inscricao);

        logStatusRepository.save(new LogStatus(inscricao, statusAnterior,
                StatusAluno.CONCLUIDO_RESPONSAVEL.name(), professor));

        return "redirect:/professor/responsavel/ofertas";
    }

    // PR.03 — encerra oferta
    @PostMapping("/encerrar-oferta/{ofertaId}")
    public String encerrarOferta(@PathVariable Long ofertaId,
                                 @RequestParam String licoesAprendidas) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        Optional<Oferta> optOferta = ofertaRepository.findById(ofertaId);
        if (!optOferta.isPresent()) {
            throw new RuntimeException("Oferta não encontrada");
        }
        Oferta oferta = optOferta.get();

        List<InscricaoOferta> inscricoes = inscricaoRepository.findByOferta(oferta);
        boolean todosConcluidos = true;
        for (InscricaoOferta i : inscricoes) {
            if (i.getStatus() != StatusAluno.CONCLUIDO_RESPONSAVEL) {
                todosConcluidos = false;
                break;
            }
        }

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
