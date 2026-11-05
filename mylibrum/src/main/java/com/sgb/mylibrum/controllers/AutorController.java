package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.AutorRequestDTO;
import com.sgb.mylibrum.dtos.response.AutorResponseDTO;
import com.sgb.mylibrum.services.AutorService;
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
@RequestMapping("/api/autores")
@RequiredArgsConstructor
@Tag(name = "Autores", description = "Cadastro e consulta de autores do acervo. Consultas exigem autenticação; criação e atualização permitem ADMIN, BIBLIOTECARIA e ASSISTENTE; exclusão permite ADMIN e BIBLIOTECARIA.")
@SecurityRequirement(name = "bearerAuth")
public class AutorController {

    private final AutorService service;

    @GetMapping
    @Operation(summary = "Listar autores", description = "Retorna os autores cadastrados. Use `ativo` para filtrar por situação; omitido, retorna todos.")
    @ApiResponse(responseCode = "200", description = "Lista de autores.")
    public ResponseEntity<List<AutorResponseDTO>> findAll(
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar autor", description = "Busca um autor pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Autor encontrado.")
    public ResponseEntity<AutorResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar autor", description = "Cria um autor. O nome é obrigatório.")
    @ApiResponse(responseCode = "201", description = "Autor cadastrado.")
    public ResponseEntity<AutorResponseDTO> create(@Valid @RequestBody AutorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar autor", description = "Substitui os dados editáveis do autor identificado.")
    @ApiResponse(responseCode = "200", description = "Autor atualizado.")
    public ResponseEntity<AutorResponseDTO> update(@PathVariable Long id, @Valid @RequestBody AutorRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir autor", description = "Remove o autor identificado, sujeito às restrições de integridade do acervo.")
    @ApiResponse(responseCode = "204", description = "Autor excluído.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}