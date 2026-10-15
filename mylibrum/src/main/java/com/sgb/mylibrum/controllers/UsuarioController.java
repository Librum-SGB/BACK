package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.request.BloqueioUsuarioRequestDTO;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> findAll(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String matriculaFuncionario,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.findAll(nome, matriculaFuncionario, ativo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}/bloqueio")
    public ResponseEntity<UsuarioResponseDTO> atualizarBloqueio(
            @PathVariable Long id, @Valid @RequestBody BloqueioUsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizarBloqueio(id, dto.getBloqueado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}