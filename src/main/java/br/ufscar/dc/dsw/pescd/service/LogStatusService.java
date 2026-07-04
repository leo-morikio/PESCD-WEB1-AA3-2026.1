package br.ufscar.dc.dsw.pescd.service;

import br.ufscar.dc.dsw.pescd.config.UsuarioLogadoUtil;
import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.LogStatus;
import br.ufscar.dc.dsw.pescd.model.Usuario;
import br.ufscar.dc.dsw.pescd.model.enums.StatusAluno;
import br.ufscar.dc.dsw.pescd.repository.LogStatusRepository;
import br.ufscar.dc.dsw.pescd.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogStatusService {

    private final LogStatusRepository logStatusRepository;

    private final UsuarioRepository usuarioRepository;

    public LogStatusService(LogStatusRepository logStatusRepository, UsuarioRepository usuarioRepository) {
        this.logStatusRepository = logStatusRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * S.03 RN-3 - Registra mudança de status de um aluno na inscrição.
     */
    public void registrar(InscricaoOferta inscricao, StatusAluno anterior, StatusAluno novo) {
        Usuario responsavel = UsuarioLogadoUtil.getUsuarioLogado(usuarioRepository);
        String statusAnterior = anterior != null ? anterior.name() : null;
        LogStatus log = new LogStatus(inscricao, statusAnterior, novo.name(), responsavel);
        logStatusRepository.save(log);
    }
}
