package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {
    Optional<Usuario> findByCpf(String cpf);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByTelefone(String telefone);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByMatriculaFuncionarioIgnoreCase(String matriculaFuncionario);
    boolean existsByMatriculaFuncionarioIgnoreCaseAndIdNot(String matriculaFuncionario, Long id);
    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Usuario> findByMatriculaFuncionarioIgnoreCase(String matriculaFuncionario, Pageable pageable);
    Page<Usuario> findByNomeContainingIgnoreCaseAndMatriculaFuncionarioIgnoreCase(
            String nome, String matriculaFuncionario, Pageable pageable);
}