package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.enums.StatusExemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long>, JpaSpecificationExecutor<Exemplar> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Exemplar e where e.id = :id")
    Optional<Exemplar> findByIdForUpdate(@Param("id") Long id);

    Optional<Exemplar> findByCodigoBarras(String codigoBarras);
    List<Exemplar> findByMaterialId(Long materialId);
    List<Exemplar> findByStatus(StatusExemplar status);
    boolean existsByEstanteId(Long estanteId);
}