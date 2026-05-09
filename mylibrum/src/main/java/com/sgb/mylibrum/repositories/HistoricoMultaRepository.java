package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.HistoricoMulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoMultaRepository extends JpaRepository<HistoricoMulta, Long> {
    List<HistoricoMulta> findByEmprestimoId(Long emprestimoId);
    List<HistoricoMulta> findByPagoFalse(); // Busca todas as multas pendentes
}