package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.enums.StatusEmprestimo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Emprestimo e where e.id = :id")
    Optional<Emprestimo> findByIdForUpdate(@Param("id") Long id);

    List<Emprestimo> findByUsuarioId(Long usuarioId);
    List<Emprestimo> findByExemplarId(Long exemplarId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Emprestimo> findByStatusInAndDataDevolucaoPrevistaBefore(
            List<StatusEmprestimo> statuses, LocalDate data);
}