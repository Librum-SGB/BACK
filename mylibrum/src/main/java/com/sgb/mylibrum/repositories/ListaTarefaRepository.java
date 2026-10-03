package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.ListaTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaTarefaRepository extends JpaRepository<ListaTarefa, Long> {
    List<ListaTarefa> findByUsuarioId(Long usuarioId);
    List<ListaTarefa> findByUsuarioIdAndConcluidaFalse(Long usuarioId);
}