package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByUsuarioId(Long usuarioId);
    List<Emprestimo> findByExemplarId(Long exemplarId);
    // Busca empréstimos que ainda não foram devolvidos
    List<Emprestimo> findByDataDevolucaoEfetivadaIsNull(); 
}