package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.ListaTarefa;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaTarefaRepository extends JpaRepository<ListaTarefa, Long>,
        JpaSpecificationExecutor<ListaTarefa> {
    List<ListaTarefa> findByUsuarioId(Long usuarioId);
    List<ListaTarefa> findByUsuarioIdAndStatusNot(Long usuarioId, StatusTarefa status);
}