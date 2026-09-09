package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.GeneroRequestDTO;
import com.sgb.mylibrum.dtos.response.GeneroResponseDTO;
import com.sgb.mylibrum.services.GeneroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/generos")
@RequiredArgsConstructor
public class GeneroController {

    private final GeneroService service;

    @GetMapping
    public ResponseEntity<List<GeneroResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneroResponseDTO> findById(@PathVariable Long id) {
        GeneroResponseDTO genero = service.findById(id);

        if (genero == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(genero);
    }

    @PostMapping
    public ResponseEntity<GeneroResponseDTO> create(@Valid @RequestBody GeneroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneroResponseDTO> update(@PathVariable Long id, @Valid @RequestBody GeneroRequestDTO dto) {
        GeneroResponseDTO genero = service.update(id, dto);
        if (genero == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(genero);
    }

    @PutMapping("/desativar/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        boolean isDesativo = service.desativar(id);
        if (isDesativo) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/ativar/{id}")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        boolean isAtivo = service.ativar(id);
        if (isAtivo) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean isDeletado = service.delete(id);
        if (isDeletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}