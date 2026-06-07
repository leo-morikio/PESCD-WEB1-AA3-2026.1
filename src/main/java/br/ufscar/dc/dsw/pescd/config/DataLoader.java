package br.ufscar.dc.dsw.pescd.config;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.model.enums.StatusOferta;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.OfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final OfertaRepository ofertaRepository;
    private final InscricaoOfertaRepository inscricaoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository,
                      OfertaRepository ofertaRepository,
                      InscricaoOfertaRepository inscricaoRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.ofertaRepository = ofertaRepository;
        this.inscricaoRepository = inscricaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String senha = passwordEncoder.encode("123");

        // Admin
        Usuario admin = new Usuario();
        admin.setNomeCompleto("Administrador");
        admin.setEmail("admin@ufscar.br");
        admin.setNomeUsuario("admin");
        admin.setSenha(senha);
        admin.setPerfil(Perfil.ADMIN);
        usuarioRepository.save(admin);

        // Secretário
        Usuario secretario = new Usuario();
        secretario.setNomeCompleto("Secretária Maria");
        secretario.setEmail("secretario@ufscar.br");
        secretario.setNomeUsuario("secretario");
        secretario.setSenha(senha);
        secretario.setPerfil(Perfil.SECRETARIO);
        usuarioRepository.save(secretario);

        // Professor responsável
        Usuario professor = new Usuario();
        professor.setNomeCompleto("Prof. André Endo");
        professor.setEmail("professor@ufscar.br");
        professor.setNomeUsuario("professor");
        professor.setSenha(senha);
        professor.setPerfil(Perfil.PROFESSOR);
        usuarioRepository.save(professor);

        // Aluno
        Usuario aluno = new Usuario();
        aluno.setNomeCompleto("Matheus Aluno");
        aluno.setEmail("aluno@ufscar.br");
        aluno.setNomeUsuario("aluno");
        aluno.setSenha(senha);
        aluno.setPerfil(Perfil.ALUNO);
        usuarioRepository.save(aluno);

        // Oferta em andamento
        Oferta oferta = new Oferta();
        oferta.setNome("PESCD 2026.1 - Engenharia de Software");
        oferta.setSemestre("2026.1");
        oferta.setDataInicio(LocalDate.of(2026, 3, 1));
        oferta.setDataFim(LocalDate.of(2026, 7, 1));
        oferta.setStatus(StatusOferta.ATIVA);
        oferta.setProfessorResponsavel(professor);
        oferta.setCriadoPor(secretario);
        oferta.setCriadoEm(LocalDateTime.of(2026, 3, 1, 9, 0));
        ofertaRepository.save(oferta);

        // Oferta concluída (exemplo)
        Oferta ofertaConcluida = new Oferta();
        ofertaConcluida.setNome("PESCD 2025.2 - Sistemas Distribuídos");
        ofertaConcluida.setSemestre("2025.2");
        ofertaConcluida.setDataInicio(LocalDate.of(2025, 8, 1));
        ofertaConcluida.setDataFim(LocalDate.of(2025, 12, 1));
        ofertaConcluida.setStatus(StatusOferta.CONCLUIDA);
        ofertaConcluida.setProfessorResponsavel(professor);
        ofertaConcluida.setCriadoPor(secretario);
        ofertaConcluida.setCriadoEm(LocalDateTime.of(2025, 8, 1, 9, 0));
        ofertaRepository.save(ofertaConcluida);

        // Inscreve o aluno na oferta em andamento
        InscricaoOferta inscricao = new InscricaoOferta();
        inscricao.setAluno(aluno);
        inscricao.setOferta(oferta);
        inscricao.setStatus(StatusAluno.NAO_ENVIADO);
        inscricaoRepository.save(inscricao);
    }
}
