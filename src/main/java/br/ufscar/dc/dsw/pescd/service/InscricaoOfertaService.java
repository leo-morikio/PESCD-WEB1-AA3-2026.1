package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.Oferta;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.Perfil;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.InscricaoOfertaRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class InscricaoOfertaService {

    @Autowired
    private InscricaoOfertaRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LogStatusService logStatusService;
    @Autowired
    private OfertaService ofertaService;

    public List<InscricaoOferta> listarPorOferta(Oferta oferta) {
        return inscricaoRepository.findByOferta(oferta);
    }

    public List<InscricaoOferta> listarPorAluno(Usuario aluno) {
        return inscricaoRepository.findByAluno(aluno);
    }

    /**
     * S.02 - Inscreve aluno na oferta.
     * RN-1: aluno não pode ser inscrito duas vezes na mesma oferta.
     */
    public void inscrever(Usuario aluno, Oferta oferta) {
        inscrever(aluno, oferta, null);
    }

    public void inscrever(Usuario aluno, Oferta oferta, Usuario supervisor) {
        inscricaoRepository.findByAlunoAndOferta(aluno, oferta).ifPresent(i -> {
            throw new RuntimeException("Aluno já está inscrito nesta oferta.");
        });
        InscricaoOferta inscricao = new InscricaoOferta();
        inscricao.setAluno(aluno);
        inscricao.setOferta(oferta);
        inscricao.setProfessorSupervisor(supervisor);
        inscricao.setStatus(StatusAluno.NAO_ENVIADO);
        inscricaoRepository.save(inscricao);

        logStatusService.registrar(inscricao, null, StatusAluno.NAO_ENVIADO);
    }

    /**
     * S.02 CSV - Formato: RA,NOME COMPLETO,EMAIL (com cabeçalho).
     * Se o aluno não existir no banco, cria com nomeUsuario=email, senha=RA.
     * Retorna lista de linhas que falharam.
     */

    public List<String> inscreverPorCsv(MultipartFile arquivo, Long ofertaId) throws IOException {
        List<String> falhas = new ArrayList<>();
        Oferta oferta = ofertaService.buscarPorId(ofertaId);
        Reader in = new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.builder().setHeader().setSkipHeaderRecord(true).build().parse(in);
        for (CSVRecord record : records) {
            String ra = record.get("RA");
            String nomeCompleto = record.get("NOME_COMPLETO");
            String email = record.get("EMAIL");
            Usuario aluno = usuarioRepository.findByEmail(email);
            if (aluno == null) {
                aluno = new Usuario();
                aluno.setNomeCompleto(nomeCompleto);
                aluno.setEmail(email);
                aluno.setNomeUsuario(email);
                aluno.setSenha(passwordEncoder.encode(ra));
                aluno.setPerfil(Perfil.ALUNO);
                usuarioRepository.save(aluno);
            }
            try {
                inscrever(aluno, oferta, null);
            } catch (RuntimeException ex) {
                falhas.add(email + " (" + ex.getMessage() + ")");
            }
        }
        return falhas;
    }

    public void excluir(Long id) {
        inscricaoRepository.deleteById(id);
    }
}
