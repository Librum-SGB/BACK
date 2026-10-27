package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.enums.StatusExemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(value = "select count(*) > 0 from exemplares where estante_id = :estanteId", nativeQuery = true)
    boolean existsIncludingDeletedByEstanteId(@Param("estanteId") Long estanteId);

    @Modifying
    @Query(value = "update exemplares set estante_id = null, prateleira = null, posicao = null where estante_id = :estanteId",
            nativeQuery = true)
    int detachAllFromEstante(@Param("estanteId") Long estanteId);

    @Query(value = "select count(*) from exemplares where estante_id = :estanteId and prateleira = :nome",
            nativeQuery = true)
    long countByEstanteIdAndPrateleiraIncludingDeleted(@Param("estanteId") Long estanteId,
                                                       @Param("nome") String nome);

    @Modifying
    @Query(value = "update exemplares set prateleira = :newName where estante_id = :estanteId and prateleira = :oldName",
            nativeQuery = true)
    int renamePrateleira(@Param("estanteId") Long estanteId,
                         @Param("oldName") String oldName,
                         @Param("newName") String newName);
}