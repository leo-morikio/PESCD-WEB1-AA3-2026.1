package br.ufscar.dc.dsw.pescd.controller.rest;

import br.ufscar.dc.dsw.pescd.dto.DocumentacaoEnsinoResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.InscreverAlunoRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.InscricaoOfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.LogStatusResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.PlanoTrabalhoResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.RelatorioFinalResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.ResultadoCsvDTO;
import br.ufscar.dc.dsw.pescd.dto.UsuarioResponseDTO;
import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import br.ufscar.dc.dsw.pescd.model.LogStatus;
import br.ufscar.dc.dsw.pescd.model.PlanoTrabalho;
import br.ufscar.dc.dsw.pescd.model.RelatorioFinal;
import br.ufscar.dc.dsw.pescd.exception.RecursoNaoEncontradoException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * S.02 - Secretário adiciona alunos à oferta (manual ou CSV), versão REST.
 * Equivalente à SecretarioAlunoController (MVC), mas retornando JSON.
 */
@RestController
@RequestMapping("/api/secretario/ofertas/{ofertaId}/alunos")
public class SecretarioAlunoRestController {

    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;
    private final InscricaoOfertaService inscricaoOfertaService;
    private final InscricaoOfertaRepository inscricaoOfertaRepository;
    private final PlanoTrabalhoRepository planoTrabalhoRepository;
    private final RelatorioFinalRepository relatorioFinalRepository;
    private final DocumentacaoEnsinoRepository documentacaoEnsinoRepository;
    private final LogStatusRepository logStatusRepository;

    public SecretarioAlunoRestController(OfertaService ofertaService, UsuarioService usuarioService, InscricaoOfertaService inscricaoOfertaService,
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

    // S.02 - Lista alunos inscritos + candidatos disponíveis (alunos e professores)
    @GetMapping
    public Map<String, Object> listar(@PathVariable Long ofertaId) {
        Oferta oferta = ofertaService.buscarPorId(ofertaId);

        List<InscricaoOferta> inscricoes = inscricaoOfertaService.listarPorOferta(oferta);
        List<InscricaoOfertaResponseDTO> inscritos = new java.util.ArrayList<>();
        List<Long> idsInscritos = new java.util.ArrayList<>();
        for (InscricaoOferta inscricao : inscricoes) {
            inscritos.add(new InscricaoOfertaResponseDTO(inscricao));
            idsInscritos.add(inscricao.getAluno().getId());
        }

        List<Usuario> todosUsuarios = usuarioService.listarTodos();
        List<UsuarioResponseDTO> alunosDisponiveis = new java.util.ArrayList<>();
        List<UsuarioResponseDTO> professores = new java.util.ArrayList<>();
        for (Usuario usuario : todosUsuarios) {
            if (usuario.getPerfil() == Perfil.ALUNO && !idsInscritos.contains(usuario.getId())) {
                alunosDisponiveis.add(new UsuarioResponseDTO(usuario));
            }
            if (usuario.getPerfil() == Perfil.PROFESSOR) {
                professores.add(new UsuarioResponseDTO(usuario));
            }
        }

        Map<String, Object> resposta = new java.util.HashMap<>();
        resposta.put("inscritos", inscritos);
        resposta.put("alunosDisponiveis", alunosDisponiveis);
        resposta.put("professores", professores);
        return resposta;
    }

    // S.03 RN-3 - Detalhes do aluno na inscrição (info + logs)
    @GetMapping("/{inscricaoId}")
    public Map<String, Object> detalhes(@PathVariable Long ofertaId, @PathVariable Long inscricaoId) {
        Optional<InscricaoOferta> optInscricao = inscricaoOfertaRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        InscricaoOferta inscricao = optInscricao.get();
        if (!inscricao.getOferta().getId().equals(ofertaId)) {
            throw new RecursoNaoEncontradoException(
                    "Inscrição " + inscricaoId + " não pertence à oferta " + ofertaId);
        }

        Optional<PlanoTrabalho> optPlano = planoTrabalhoRepository.findByInscricao(inscricao);
        PlanoTrabalhoResponseDTO planoDto = null;
        if (optPlano.isPresent()) {
            planoDto = new PlanoTrabalhoResponseDTO(optPlano.get());
        }

        Optional<RelatorioFinal> optRelatorio = relatorioFinalRepository.findByInscricao(inscricao);
        RelatorioFinalResponseDTO relatorioDto = null;
        if (optRelatorio.isPresent()) {
            relatorioDto = new RelatorioFinalResponseDTO(optRelatorio.get());
        }

        Optional<DocumentacaoEnsino> optDocumentacao = documentacaoEnsinoRepository.findByInscricao(inscricao);
        DocumentacaoEnsinoResponseDTO documentacaoDto = null;
        if (optDocumentacao.isPresent()) {
            documentacaoDto = new DocumentacaoEnsinoResponseDTO(optDocumentacao.get());
        }

        List<LogStatus> logs = logStatusRepository.findByInscricao(inscricao);
        List<LogStatusResponseDTO> logsDto = new java.util.ArrayList<>();
        for (LogStatus log : logs) {
            logsDto.add(new LogStatusResponseDTO(log));
        }

        Map<String, Object> resposta = new java.util.HashMap<>();
        resposta.put("inscricao", new InscricaoOfertaResponseDTO(inscricao));
        resposta.put("plano", planoDto);
        resposta.put("relatorio", relatorioDto);
        resposta.put("documentacao", documentacaoDto);
        resposta.put("logs", logsDto);
        return resposta;
    }

    // S.02 - Inscrição manual (selecionar aluno pelo ID)
    @PostMapping
    public ResponseEntity<InscricaoOfertaResponseDTO> inscrever(@PathVariable Long ofertaId,
                                                                @RequestBody InscreverAlunoRequestDTO dto) {
        Oferta oferta = ofertaService.buscarPorId(ofertaId);
        Usuario aluno = usuarioService.buscarPorId(dto.getAlunoId());
        if (dto.getSupervisorId() != null) {
            Usuario supervisor = usuarioService.buscarPorId(dto.getSupervisorId());
            inscricaoOfertaService.inscrever(aluno, oferta, supervisor);
        } else {
            inscricaoOfertaService.inscrever(aluno, oferta);
        }

        Optional<InscricaoOferta> optInscricao = inscricaoOfertaRepository.findByAlunoAndOferta(aluno, oferta);
        InscricaoOferta inscricao = optInscricao.get();
        return ResponseEntity.status(HttpStatus.CREATED).body(new InscricaoOfertaResponseDTO(inscricao));
    }

    // S.02 - Inscrição por upload (usando um csv)
    @PostMapping("/csv")
    public ResultadoCsvDTO uploadCsv(@PathVariable Long ofertaId,
                                     @RequestParam("arquivo") MultipartFile arquivo) throws IOException {
        return inscricaoOfertaService.inscreverPorCsv(arquivo, ofertaId);
    }

    // S.02 - Remove aluno da oferta
    @DeleteMapping("/{inscricaoId}")
    public ResponseEntity<Void> remover(@PathVariable Long ofertaId, @PathVariable Long inscricaoId) {
        Optional<InscricaoOferta> optInscricao = inscricaoOfertaRepository.findById(inscricaoId);
        if (!optInscricao.isPresent()) {
            throw new RecursoNaoEncontradoException("Inscrição não encontrada: " + inscricaoId);
        }
        if (!optInscricao.get().getOferta().getId().equals(ofertaId)) {
            throw new RecursoNaoEncontradoException(
                    "Inscrição " + inscricaoId + " não pertence à oferta " + ofertaId);
        }
        inscricaoOfertaService.excluir(inscricaoId);
        return ResponseEntity.noContent().build();
    }
}
