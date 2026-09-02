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
        log.info("Genero: {}", genero);

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
        if (genero == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(genero);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean isDeletado = service.delete(id);
        if (isDeletado) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.notFound().build();
    }
}