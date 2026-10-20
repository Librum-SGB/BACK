package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.HistoricoMultaRequestDTO;
import com.sgb.mylibrum.dtos.response.HistoricoMultaResponseDTO;
import com.sgb.mylibrum.entities.enums.StatusMulta;
import com.sgb.mylibrum.security.AutorizacaoService;
import com.sgb.mylibrum.services.HistoricoMultaService;
import jakarta.validation.Valid;
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
    private final AutorizacaoService autorizacao;

    @GetMapping
    public ResponseEntity<List<HistoricoMultaResponseDTO>> findAll(
            @RequestParam(required = false) StatusMulta status,
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(status, ativo));
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<List<HistoricoMultaResponseDTO>> findByUsuarioId(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean ativo) {
        autorizacao.exigirProprioOuEquipe(id);
        return ResponseEntity.ok(service.findByUsuarioId(id, ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HistoricoMultaResponseDTO> findById(@PathVariable Long id) {
            HistoricoMultaResponseDTO multa = service.findById(id);
            autorizacao.exigirProprioOuEquipe(multa.getUsuarioId());
            return ResponseEntity.ok(multa);
    }

    @PostMapping
    public ResponseEntity<HistoricoMultaResponseDTO> create(@Valid @RequestBody HistoricoMultaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{id}/pagamento")
    public ResponseEntity<HistoricoMultaResponseDTO> registrarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(service.registrarPagamento(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HistoricoMultaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody HistoricoMultaRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}