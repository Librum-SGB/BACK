package com.sgb.mylibrum.repositories;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sgb.mylibrum.entities.TokenRecuperacaoSenha;

@Repository
public interface TokenRecuperacaoSenhaRepository extends JpaRepository<TokenRecuperacaoSenha, Long> {

    // O join fetch respeita a restrição de exclusão lógica: tokens de usuários excluídos não são encontrados.
    @Query("select t from TokenRecuperacaoSenha t join fetch t.usuario where t.tokenHash = :tokenHash")
    Optional<TokenRecuperacaoSenha> findByTokenHash(@Param("tokenHash") String tokenHash);

    boolean existsByUsuarioIdAndDataCriacaoAfter(Long usuarioId, OffsetDateTime limite);

    void deleteByUsuarioIdAndUsadoEmIsNull(Long usuarioId);
}
