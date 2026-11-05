package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.EditoraRequestDTO;
import com.sgb.mylibrum.dtos.response.EditoraResponseDTO;
import com.sgb.mylibrum.services.EditoraService;
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
@RequestMapping("/api/editoras")
@RequiredArgsConstructor
@Tag(name = "Editoras", description = "Cadastro e consulta das editoras associadas aos materiais. Consultas exigem autenticação; criação e atualização permitem ADMIN, BIBLIOTECARIA e ASSISTENTE; exclusão permite ADMIN e BIBLIOTECARIA.")
@SecurityRequirement(name = "bearerAuth")
public class EditoraController {

    private final EditoraService service;

    @GetMapping
    @Operation(summary = "Listar editoras", description = "Retorna as editoras cadastradas; permite filtrar pela situação ativa.")
    @ApiResponse(responseCode = "200", description = "Lista de editoras.")
    public ResponseEntity<List<EditoraResponseDTO>> findAll(
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar editora", description = "Busca uma editora pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Editora encontrada.")
    public ResponseEntity<EditoraResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar editora", description = "Cria uma editora. O nome é obrigatório.")
    @ApiResponse(responseCode = "201", description = "Editora cadastrada.")
    public ResponseEntity<EditoraResponseDTO> create(@Valid @RequestBody EditoraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar editora", description = "Atualiza os dados da editora identificada.")
    @ApiResponse(responseCode = "200", description = "Editora atualizada.")
    public ResponseEntity<EditoraResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EditoraRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir editora", description = "Remove a editora identificada, desde que permitido pelas referências do acervo.")
    @ApiResponse(responseCode = "204", description = "Editora excluída.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}