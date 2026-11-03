package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.FilialRequestDTO;
import com.sgb.mylibrum.dtos.response.FilialResponseDTO;
import com.sgb.mylibrum.services.FilialService;
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
@RequestMapping("/api/filiais")
@RequiredArgsConstructor
@Tag(name = "Filiais", description = "Unidades da biblioteca. Consultas são permitidas à equipe; alterações são restritas a administradores.")
@SecurityRequirement(name = "bearerAuth")
public class FilialController {

    private final FilialService service;

    @GetMapping
    @Operation(summary = "Listar filiais", description = "Retorna as filiais cadastradas, com filtro opcional por situação.")
    @ApiResponse(responseCode = "200", description = "Lista de filiais.")
    public ResponseEntity<List<FilialResponseDTO>> findAll(
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar filial", description = "Busca uma filial pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Filial encontrada.")
    public ResponseEntity<FilialResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar filial", description = "Cria uma filial. Nome fantasia, razão social, CNPJ e endereço são obrigatórios.")
    @ApiResponse(responseCode = "201", description = "Filial cadastrada.")
    public ResponseEntity<FilialResponseDTO> create(@Valid @RequestBody FilialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar filial", description = "Atualiza os dados cadastrais da filial identificada.")
    @ApiResponse(responseCode = "200", description = "Filial atualizada.")
    public ResponseEntity<FilialResponseDTO> update(@PathVariable Long id, @Valid @RequestBody FilialRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir filial", description = "Remove a filial identificada, sujeita às restrições de integridade.")
    @ApiResponse(responseCode = "204", description = "Filial excluída.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}