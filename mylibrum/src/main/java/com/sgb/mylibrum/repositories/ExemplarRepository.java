package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.enums.StatusExemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    Optional<Exemplar> findByCodigoBarras(String codigoBarras);
    List<Exemplar> findByLivroId(Long livroId);
    List<Exemplar> findByStatus(StatusExemplar status);
}