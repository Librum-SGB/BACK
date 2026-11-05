package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.GeneroRequestDTO;
import com.sgb.mylibrum.dtos.response.GeneroResponseDTO;
import com.sgb.mylibrum.services.GeneroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/generos")
@RequiredArgsConstructor
@Tag(name = "Gêneros", description = "Gêneros literários associados aos materiais; inclui operações de ativação e desativação. Consultas exigem autenticação; criação e atualização permitem ADMIN, BIBLIOTECARIA e ASSISTENTE; exclusão permite ADMIN e BIBLIOTECARIA.")
@SecurityRequirement(name = "bearerAuth")
public class GeneroController {

    private final GeneroService service;

    @GetMapping
    @Operation(summary = "Listar gêneros", description = "Retorna os gêneros cadastrados, com filtro opcional por situação.")
    @ApiResponse(responseCode = "200", description = "Lista de gêneros.")
    public ResponseEntity<List<GeneroResponseDTO>> findAll(
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar gênero", description = "Busca um gênero pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Gênero encontrado.")
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado.")
    public ResponseEntity<GeneroResponseDTO> findById(@PathVariable Long id) {
        GeneroResponseDTO genero = service.findById(id);

        if (genero == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(genero);
    }

    @PostMapping
    @Operation(summary = "Cadastrar gênero", description = "Cria um gênero. O nome é obrigatório e limitado a 100 caracteres.")
    @ApiResponse(responseCode = "201", description = "Gênero cadastrado.")
    public ResponseEntity<GeneroResponseDTO> create(@Valid @RequestBody GeneroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar gênero", description = "Atualiza o gênero identificado.")
    @ApiResponse(responseCode = "200", description = "Gênero atualizado.")
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado.")
    public ResponseEntity<GeneroResponseDTO> update(@PathVariable Long id, @Valid @RequestBody GeneroRequestDTO dto) {
        GeneroResponseDTO genero = service.update(id, dto);
        if (genero == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(genero);
    }

    @PutMapping("/desativar/{id}")
    @Operation(summary = "Desativar gênero", description = "Marca o gênero como inativo sem removê-lo do cadastro.")
    @ApiResponse(responseCode = "204", description = "Gênero desativado.")
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado.")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        boolean isDesativo = service.desativar(id);
        if (isDesativo) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/ativar/{id}")
    @Operation(summary = "Ativar gênero", description = "Marca novamente como ativo um gênero previamente desativado.")
    @ApiResponse(responseCode = "204", description = "Gênero ativado.")
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado.")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        boolean isAtivo = service.ativar(id);
        if (isAtivo) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir gênero", description = "Remove o gênero identificado.")
    @ApiResponse(responseCode = "204", description = "Gênero excluído.")
    @ApiResponse(responseCode = "404", description = "Gênero não encontrado.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean isDeletado = service.delete(id);
        if (isDeletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}