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
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final OfertaRepository ofertaRepository;
    private final InscricaoOfertaRepository inscricaoRepository;

    public DataLoader(UsuarioRepository usuarioRepository,
                      OfertaRepository ofertaRepository,
                      InscricaoOfertaRepository inscricaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.ofertaRepository = ofertaRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    @Override
    public void run(String... args) {
        // Cria um professor responsável
        Usuario professor = new Usuario();
        professor.setNomeCompleto("Prof. André Endo");
        professor.setEmail("professor@ufscar.br");
        professor.setNomeUsuario("professor");
        professor.setSenha("123");
        professor.setPerfil(Perfil.PROFESSOR);
        usuarioRepository.save(professor);

        // Cria um aluno
        Usuario aluno = new Usuario();
        aluno.setNomeCompleto("Matheus Aluno");
        aluno.setEmail("aluno@ufscar.br");
        aluno.setNomeUsuario("aluno");
        aluno.setSenha("123");
        aluno.setPerfil(Perfil.ALUNO);
        usuarioRepository.save(aluno);

        // Cria uma oferta
        Oferta oferta = new Oferta();
        oferta.setNome("PESCD 2026.1 - Engenharia de Software");
        oferta.setSemestre("2026.1");
        oferta.setDataInicio(LocalDate.of(2026, 3, 1));
        oferta.setDataFim(LocalDate.of(2026, 7, 1));
        oferta.setStatus(StatusOferta.ATIVA);
        oferta.setProfessorResponsavel(professor);
        ofertaRepository.save(oferta);

        // Inscreve o aluno na oferta
        InscricaoOferta inscricao = new InscricaoOferta();
        inscricao.setAluno(aluno);
        inscricao.setOferta(oferta);
        inscricao.setStatus(StatusAluno.NAO_ENVIADO);
        inscricaoRepository.save(inscricao);
    }
}