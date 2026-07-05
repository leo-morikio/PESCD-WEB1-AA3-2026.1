package br.ufscar.dc.dsw.pescd.dto;

import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;

/**
 * Payload de entrada para criação/atualização de Usuario via API (AD.01).
 * A senha é opcional na atualização (mantém a senha atual quando em branco).
 */
public class UsuarioRequestDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String nomeUsuario;
    private String senha;
    private Perfil perfil;

    public Usuario paraEntidade() {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNomeCompleto(nomeCompleto);
        usuario.setEmail(email);
        usuario.setNomeUsuario(nomeUsuario);
        usuario.setSenha(senha);
        usuario.setPerfil(perfil);
        return usuario;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}
