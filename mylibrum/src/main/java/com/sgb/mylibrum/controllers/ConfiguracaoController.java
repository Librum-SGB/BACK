package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.response.ConfiguracaoResponseDTO;
import com.sgb.mylibrum.services.ConfiguracaoService;
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
@RequestMapping("/api/configuracoes")
@RequiredArgsConstructor
@Tag(name = "Configurações", description = "Parâmetros configuráveis vinculados a uma filial. Acesso restrito a administradores.")
@SecurityRequirement(name = "bearerAuth")
public class ConfiguracaoController {

    private final ConfiguracaoService service;

    @GetMapping
    @Operation(summary = "Listar configurações", description = "Retorna as configurações cadastradas, opcionalmente filtradas pela situação.")
    @ApiResponse(responseCode = "200", description = "Lista de configurações.")
    public ResponseEntity<List<ConfiguracaoResponseDTO>> findAll(
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar configuração", description = "Busca uma configuração pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Configuração encontrada.")
    public ResponseEntity<ConfiguracaoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Criar configuração", description = "Cria uma configuração vinculada a uma filial. Filial, chave e valor são obrigatórios.")
    @ApiResponse(responseCode = "201", description = "Configuração criada.")
    public ResponseEntity<ConfiguracaoResponseDTO> create(@Valid @RequestBody ConfiguracaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar configuração", description = "Atualiza os dados da configuração identificada.")
    @ApiResponse(responseCode = "200", description = "Configuração atualizada.")
    public ResponseEntity<ConfiguracaoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ConfiguracaoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir configuração", description = "Remove a configuração identificada.")
    @ApiResponse(responseCode = "204", description = "Configuração excluída.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}