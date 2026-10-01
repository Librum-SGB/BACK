package com.sgb.mylibrum.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sgb.mylibrum.entities.Gestor;

@Repository
public interface GestorRepository extends JpaRepository<Gestor, Long> {
    Optional<Gestor> findByEmail(String email);
    Optional<Gestor> findByMatriculaFuncionario(String matriculaFuncionario);
}