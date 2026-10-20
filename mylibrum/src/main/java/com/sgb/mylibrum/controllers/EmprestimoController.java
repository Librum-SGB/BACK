package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.EmprestimoRequestDTO;
import com.sgb.mylibrum.dtos.response.EmprestimoResponseDTO;
import com.sgb.mylibrum.security.AutorizacaoService;
import com.sgb.mylibrum.services.EmprestimoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
public class EmprestimoController {

    private final EmprestimoService service;
    private final AutorizacaoService autorizacao;

    @GetMapping
    public ResponseEntity<List<EmprestimoResponseDTO>> findAll(
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<List<EmprestimoResponseDTO>> findByUsuarioId(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean ativo) {
        autorizacao.exigirProprioOuEquipe(id);
        return ResponseEntity.ok(service.findByUsuarioId(id, ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> findById(@PathVariable Long id) {
        EmprestimoResponseDTO emprestimo = service.findById(id);
        autorizacao.exigirProprioOuEquipe(emprestimo.getUsuarioId());
        return ResponseEntity.ok(emprestimo);
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> create(@Valid @RequestBody EmprestimoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PostMapping("/{id}/devolucao")
    public ResponseEntity<EmprestimoResponseDTO> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(service.devolver(id));
    }

    @PostMapping("/{id}/renovacao")
    public ResponseEntity<EmprestimoResponseDTO> renovar(@PathVariable Long id) {
        return ResponseEntity.ok(service.renovar(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmprestimoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EmprestimoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}