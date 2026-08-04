package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.HistoricoMultaDTO;
import com.sgb.mylibrum.services.HistoricoMultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historico-multas")
@RequiredArgsConstructor
public class HistoricoMultaController {

    private final HistoricoMultaService service;

    @GetMapping
    public ResponseEntity<List<HistoricoMultaDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricoMultaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<HistoricoMultaDTO> create(@RequestBody HistoricoMultaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoricoMultaDTO> update(@PathVariable Long id, @RequestBody HistoricoMultaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}