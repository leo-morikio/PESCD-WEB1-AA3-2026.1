package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;

/**
 * Representação de Usuario exposta pela API — nunca inclui a senha.
 */
public class UsuarioResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String nomeUsuario;
    private Perfil perfil;

    public UsuarioResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nomeCompleto = usuario.getNomeCompleto();
        this.email = usuario.getEmail();
        this.nomeUsuario = usuario.getNomeUsuario();
        this.perfil = usuario.getPerfil();
    }

    public Long getId() { return id; }
    public String getNomeCompleto() { return nomeCompleto; }
    public String getEmail() { return email; }
    public String getNomeUsuario() { return nomeUsuario; }
    public Perfil getPerfil() { return perfil; }
}
