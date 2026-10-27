package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.services.ExemplarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sgb.mylibrum.entities.enums.StatusExemplar;
import com.sgb.mylibrum.specifications.ExemplarFiltro;
import com.sgb.mylibrum.specifications.FiltrosUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exemplares")
@RequiredArgsConstructor
public class ExemplarController {

    private final ExemplarService service;

    @GetMapping
    public ResponseEntity<List<ExemplarResponseDTO>> findAll(
            @RequestParam(required = false) StatusExemplar status,
            @RequestParam(required = false) Long materialId,
            @RequestParam(required = false) Long filialId,
            @RequestParam(required = false) Long estanteId,
            @RequestParam(required = false) String codigoBarras,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAquisicaoDe,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAquisicaoAte,
            @RequestParam(required = false) String ativo) {
        ExemplarFiltro filtro = new ExemplarFiltro(status, materialId, filialId, estanteId,
                FiltrosUtil.textoOuNulo(codigoBarras), dataAquisicaoDe, dataAquisicaoAte,
                FiltrosUtil.interpretarAtivo(ativo));
        return ResponseEntity.ok(service.findAll(filtro));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExemplarResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ExemplarResponseDTO> create(@Valid @RequestBody ExemplarRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExemplarResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ExemplarRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}