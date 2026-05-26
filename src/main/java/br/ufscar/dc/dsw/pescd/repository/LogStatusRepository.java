package br.ufscar.dc.dsw.pescd.repository;

import br.ufscar.dc.dsw.pescd.model.LogStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogStatusRepository extends JpaRepository<LogStatus, Long> {
}