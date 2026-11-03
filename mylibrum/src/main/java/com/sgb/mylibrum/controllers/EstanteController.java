package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.services.EstanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/estantes")
@RequiredArgsConstructor
@Tag(name = "Estantes", description = "Estantes físicas de uma filial. Consultas são restritas à equipe.")
@SecurityRequirement(name = "bearerAuth")
public class EstanteController {

    private final EstanteService service;

    @GetMapping
    @Operation(summary = "Listar estantes", description = "Retorna as estantes cadastradas, com filtro opcional por situação.")
    @ApiResponse(responseCode = "200", description = "Lista de estantes.")
    public ResponseEntity<List<EstanteResponseDTO>> findAll(
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/filiais/{id}")
    @Operation(summary = "Listar estantes de uma filial", description = "Retorna as estantes vinculadas à filial informada; permite filtrar pela situação.")
    @ApiResponse(responseCode = "200", description = "Lista de estantes da filial.")
    public ResponseEntity<List<EstanteResponseDTO>> findByFilialId(
            @Parameter(description = "Identificador da filial.")
            @PathVariable Long id,
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findByFilialId(id, ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar estante", description = "Busca uma estante pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Estante encontrada.")
    public ResponseEntity<EstanteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar estante", description = "Cria uma estante associada a uma filial. Filial, nome e localização são obrigatórios.")
    @ApiResponse(responseCode = "201", description = "Estante cadastrada.")
    public ResponseEntity<EstanteResponseDTO> create(@Valid @RequestBody EstanteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar estante", description = "Atualiza os dados da estante identificada.")
    @ApiResponse(responseCode = "200", description = "Estante atualizada.")
    public ResponseEntity<EstanteResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EstanteRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir estante", description = "Remove a estante identificada, desde que permitido pelas referências de exemplares.")
    @ApiResponse(responseCode = "204", description = "Estante excluída.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}