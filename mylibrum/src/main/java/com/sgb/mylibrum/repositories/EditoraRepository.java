package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Editora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditoraRepository extends JpaRepository<Editora, Long>, JpaSpecificationExecutor<Editora> {
    Optional<Editora> findByNome(String nome);
}