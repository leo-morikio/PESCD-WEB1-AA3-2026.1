package br.ufscar.dc.dsw.pescd.controller.rest;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.dto.UsuarioRequestDTO;
import br.ufscar.dc.dsw.pescd.dto.UsuarioResponseDTO;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import br.ufscar.dc.dsw.pescd.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AD.01 - Administrador gerencia usuários do sistema (versão REST).
 * Equivalente à AdminUsuarioController (MVC), mas retornando JSON.
 */
@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioRestController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public AdminUsuarioRestController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    // AD.01 - Lista todos os usuários
    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        List<UsuarioResponseDTO> resposta = new java.util.ArrayList<>();
        for (Usuario usuario : usuarios) {
            resposta.add(new UsuarioResponseDTO(usuario));
        }
        return resposta;
    }

    // AD.01 - Detalhes de um usuário
    @GetMapping("/{id}")
    public UsuarioResponseDTO buscar(@PathVariable Long id) {
        return new UsuarioResponseDTO(usuarioService.buscarPorId(id));
    }

    // AD.01 - Cadastra novo usuário
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = dto.paraEntidade();
        usuario.setId(null);
        usuarioService.salvar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioResponseDTO(usuario));
    }

    // AD.01 - Atualiza usuário existente
    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(@PathVariable Long id, @RequestBody UsuarioRequestDTO dto) {
        Usuario usuario = dto.paraEntidade();
        usuario.setId(id);
        usuarioService.salvar(usuario);
        return new UsuarioResponseDTO(usuarioService.buscarPorId(id));
    }

    // AD.01 RN-3 - Exclui usuário (não pode ser o próprio admin logado)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Long idLogado = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository).getId();
        usuarioService.excluir(id, idLogado);
        return ResponseEntity.noContent().build();
    }
}
