package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.ExemplarDTO;
import com.sgb.mylibrum.services.ExemplarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exemplares")
@RequiredArgsConstructor
public class ExemplarController {

    private final ExemplarService service;

    @GetMapping
    public ResponseEntity<List<ExemplarDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExemplarDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ExemplarDTO> create(@RequestBody ExemplarDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExemplarDTO> update(@PathVariable Long id, @RequestBody ExemplarDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}