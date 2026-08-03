package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    Optional<Livro> findByIsbn10(String isbn10);
    Optional<Livro> findByIsbn13(String isbn13);
    List<Livro> findByTituloContainingIgnoreCase(String titulo);
    List<Livro> findByEditoraId(Long editoraId);
}