package br.ufscar.dc.dsw.pescd.controller.rest;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.dto.EncerrarOfertaRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.OfertaRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.OfertaResponseDTO;
import br.ufscar.dc.dsw.pescd.exception.NegocioException;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusOferta;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.OfertaService;
import br.ufscar.dc.dsw.pescd.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * S.01, S.03, S.04 - Secretário gerencia ofertas (versão REST).
 * Equivalente à SecretarioController (MVC), mas retornando JSON.
 */
@RestController
@RequestMapping("/api/secretario/ofertas")
public class SecretarioOfertaRestController {

    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public SecretarioOfertaRestController(OfertaService ofertaService, UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // S.03 - Lista ofertas (painel do secretário)
    @GetMapping
    public List<OfertaResponseDTO> listar() {
        List<Oferta> ofertas = ofertaService.listarTodasOrdenadas();
        List<OfertaResponseDTO> resposta = new java.util.ArrayList<>();
        for (Oferta oferta : ofertas) {
            resposta.add(new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta)));
        }
        return resposta;
    }

    // S.03 - Detalhes de uma oferta
    @GetMapping("/{id}")
    public OfertaResponseDTO detalhes(@PathVariable Long id) {
        Oferta oferta = ofertaService.buscarPorId(id);
        return new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta));
    }

    // S.01 - Cria nova oferta
    @PostMapping
    public ResponseEntity<OfertaResponseDTO> criar(@RequestBody OfertaRequestDTO dto) {
        Oferta oferta = dto.paraEntidade();
        if (dto.getProfessorResponsavelId() != null) {
            oferta.setProfessorResponsavel(usuarioService.buscarPorId(dto.getProfessorResponsavelId()));
        }
        oferta.setStatus(StatusOferta.ATIVA);

        Usuario secretario = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        ofertaService.salvar(oferta, secretario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta)));
    }

    // S.04 - Confirmar encerramento
    @PostMapping("/{id}/encerrar")
    public OfertaResponseDTO encerrar(@PathVariable Long id, @RequestBody EncerrarOfertaRequestDTO dto) {
        Oferta oferta = ofertaService.buscarPorId(id);
        if (oferta.getStatus() != StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO) {
            throw new NegocioException("A oferta não está aguardando encerramento.");
        }
        Usuario secretario = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        oferta.setStatus(StatusOferta.CONCLUIDA);
        oferta.setEncerradoPor(secretario);
        oferta.setEncerradoEm(LocalDateTime.now());
        oferta.setLicoesAprendidas(dto.getLicoesAprendidas());
        ofertaService.salvar(oferta, secretario);
        return new OfertaResponseDTO(oferta, ofertaService.contarAlunos(oferta));
    }
}
