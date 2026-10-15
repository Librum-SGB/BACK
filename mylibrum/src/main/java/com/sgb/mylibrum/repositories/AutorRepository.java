package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutorRepository extends JpaRepository<Autor, Long>, JpaSpecificationExecutor<Autor> {
    List<Autor> findByNomeContainingIgnoreCase(String nome);
}