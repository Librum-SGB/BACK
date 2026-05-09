package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Gestor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GestorRepository extends JpaRepository<Gestor, Long> {
    Optional<Gestor> findByLogin(String login);
    Optional<Gestor> findByMatriculaFuncionario(String matriculaFuncionario);
}