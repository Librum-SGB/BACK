package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Estante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstanteRepository extends JpaRepository<Estante, Long> {
}