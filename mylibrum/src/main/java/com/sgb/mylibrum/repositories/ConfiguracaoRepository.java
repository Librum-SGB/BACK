package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Long> {
    // Garante a busca da chave única baseada na filial e no nome da configuração
    Optional<Configuracao> findByFilialIdAndChave(Long filialId, String chave);
}