package br.ufscar.dc.dsw.pescd.repository;

import br.ufscar.dc.dsw.pescd.model.InscricaoOferta;
import br.ufscar.dc.dsw.pescd.model.LogStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogStatusRepository extends JpaRepository<LogStatus, Long> {
    List<LogStatus> findByInscricao(InscricaoOferta inscricao);
    void deleteByInscricao(InscricaoOferta inscricao);
}