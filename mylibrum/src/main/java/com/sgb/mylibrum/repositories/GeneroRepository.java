package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
    Optional<Genero> findByNome(String nome);
    boolean existsByNome(String nome);
    List<Genero> findAllByAtivoTrue();
    List<Genero> findAllByAtivoTrueAndExcluidoFalse();
    Optional<Genero> findByIdAndAtivoTrueAndExcluidoFalse(Long id);

}