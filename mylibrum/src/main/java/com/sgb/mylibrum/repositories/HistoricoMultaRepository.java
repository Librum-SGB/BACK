package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.HistoricoMulta;
import com.sgb.mylibrum.entities.enums.StatusMulta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoricoMultaRepository extends JpaRepository<HistoricoMulta, Long>,
        JpaSpecificationExecutor<HistoricoMulta> {
    List<HistoricoMulta> findByEmprestimoId(Long emprestimoId);
    List<HistoricoMulta> findByEmprestimoIdAndStatus(Long emprestimoId, StatusMulta status);
    List<HistoricoMulta> findByUsuarioId(Long usuarioId);
    List<HistoricoMulta> findByStatus(StatusMulta status);
    boolean existsByUsuarioIdAndStatus(Long usuarioId, StatusMulta status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from HistoricoMulta m where m.id = :id")
    Optional<HistoricoMulta> findByIdForUpdate(@Param("id") Long id);
}