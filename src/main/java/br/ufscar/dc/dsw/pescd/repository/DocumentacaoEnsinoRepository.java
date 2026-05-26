package br.ufscar.dc.dsw.pescd.repository;

import br.ufscar.dc.dsw.pescd.model.DocumentacaoEnsino;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentacaoEnsinoRepository extends JpaRepository<DocumentacaoEnsino, Long> {
}