package com.sgb.mylibrum.repositories;

import com.sgb.mylibrum.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    Optional<Material> findByIsbn(String isbn);
    List<Material> findByTituloContainingIgnoreCase(String titulo);
    List<Material> findByEditoraId(Long editoraId);
}
