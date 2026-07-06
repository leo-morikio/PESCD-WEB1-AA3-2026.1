package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.dto.AnalisarDocumentacaoRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.ConcluirRelatorioRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.EncerramentoOfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.EstatisticasOfertaDTO;
import br.ufscar.dc.dsw.pescd.dto.InscricaoOfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.dto.OfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.OfertaService;
import br.ufscar.dc.dsw.pescd.service.ProfessorResponsavelService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PR.01, PR.02, PR.03 - Professor responsável acompanha e encerra ofertas (versão REST).
 */
@RestController
@RequestMapping("/api/professor/responsavel")
public class ProfessorResponsavelController {

    private final ProfessorResponsavelService responsavelService;
    private final OfertaService ofertaService;
    private final UsuarioRepository usuarioRepository;

    public ProfessorResponsavelController(ProfessorResponsavelService responsavelService,
                                          OfertaService ofertaService,
                                          UsuarioRepository usuarioRepository) {
        this.responsavelService = responsavelService;
        this.ofertaService = ofertaService;
        this.usuarioRepository = usuarioRepository;
    }

    // lista ofertas do professor responsável
    @GetMapping("/ofertas")
    public List<OfertaResponseDTO> listarOfertas() {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        List<Oferta> ofertas = responsavelService.listarOfertas(professor);
        List<OfertaResponseDTO> resposta = new ArrayList<>();
        for (Oferta oferta : ofertas) {
            resposta.add(new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta)));
        }
        return resposta;
    }

    // PR.01 - conclui relatório (RN-3: exige parecer, frequência e nota)
    @PutMapping("/concluir-relatorio/{inscricaoId}")
    public InscricaoOfertaResponseDTO concluirRelatorio(@PathVariable Long inscricaoId,
                                                        @RequestBody ConcluirRelatorioRequestDTO dto) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = responsavelService.concluirRelatorio(
                inscricaoId, dto.getParecer(), dto.getFrequencia(), dto.getNota(), professor);
        return new InscricaoOfertaResponseDTO(inscricao);
    }

    // PR.02 - analisa documentação (RN-3: exige parecer, indicador de frequência e nota)
    @PutMapping("/analisar-documentacao/{inscricaoId}")
    public InscricaoOfertaResponseDTO analisarDocumentacao(@PathVariable Long inscricaoId,
                                                           @RequestBody AnalisarDocumentacaoRequestDTO dto) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = responsavelService.analisarDocumentacao(
                inscricaoId, dto.getParecer(), dto.getIndicadorFrequencia(), dto.getNota(), professor);
        return new InscricaoOfertaResponseDTO(inscricao);
    }

    // PR.03 - encerra oferta (RN-2: retorna estatísticas dos alunos concluídos)
    @PutMapping("/encerrar-oferta/{ofertaId}")
    public EncerramentoOfertaResponseDTO encerrarOferta(@PathVariable Long ofertaId,
                                                        @RequestBody Map<String, String> body) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        String licoesAprendidas = body.get("licoesAprendidas");

        EstatisticasOfertaDTO estatisticas = responsavelService.calcularEstatisticas(ofertaService.buscarPorId(ofertaId));
        Oferta oferta = responsavelService.encerrarOferta(ofertaId, licoesAprendidas, professor);
        OfertaResponseDTO ofertaDto = new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta));
        return new EncerramentoOfertaResponseDTO(ofertaDto, estatisticas);
    }
}
