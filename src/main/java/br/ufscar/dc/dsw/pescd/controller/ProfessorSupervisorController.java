package br.ufscar.dc.dsw.pescd.controller;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.dto.AprovarPlanoRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.AprovarRelatorioRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.InscricaoOfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.ProfessorSupervisorService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * PS.01, PS.02, PS.03 - Professor supervisor acompanha e aprova planos/relatórios (versão REST).
 */
@RestController
@RequestMapping("/api/professor/supervisor")
public class ProfessorSupervisorController {

    private final ProfessorSupervisorService supervisorService;
    private final UsuarioRepository usuarioRepository;

    public ProfessorSupervisorController(ProfessorSupervisorService supervisorService,
                                         UsuarioRepository usuarioRepository) {
        this.supervisorService = supervisorService;
        this.usuarioRepository = usuarioRepository;
    }

    // PS.01 - lista inscrições supervisionadas
    @GetMapping("/inscricoes")
    public List<InscricaoOfertaResponseDTO> listarInscricoes() {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        List<InscricaoOferta> inscricoes = supervisorService.listarInscricoes(professor);
        List<InscricaoOfertaResponseDTO> resposta = new ArrayList<>();
        for (InscricaoOferta inscricao : inscricoes) {
            resposta.add(new InscricaoOfertaResponseDTO(inscricao));
        }
        return resposta;
    }

    // PS.02 - aprova plano (RN-3: exige parecer)
    @PutMapping("/aprovar-plano/{inscricaoId}")
    public InscricaoOfertaResponseDTO aprovarPlano(@PathVariable Long inscricaoId,
                                                   @RequestBody AprovarPlanoRequestDTO dto) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = supervisorService.aprovarPlano(inscricaoId, dto.getParecer(), professor);
        return new InscricaoOfertaResponseDTO(inscricao);
    }

    // PS.03 - aprova relatório (RN-3: exige parecer, indicador de frequência e sugestão de nota)
    @PutMapping("/aprovar-relatorio/{inscricaoId}")
    public InscricaoOfertaResponseDTO aprovarRelatorio(@PathVariable Long inscricaoId,
                                                       @RequestBody AprovarRelatorioRequestDTO dto) {
        Usuario professor = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        InscricaoOferta inscricao = supervisorService.aprovarRelatorio(
                inscricaoId, dto.getParecer(), dto.getIndicadorFrequencia(), dto.getNotaFinal(), professor);
        return new InscricaoOfertaResponseDTO(inscricao);
    }
}
