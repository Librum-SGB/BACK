package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilialRepository extends JpaRepository<Filial, Long> {
    Optional<Filial> findByCnpj(String cnpj);
}