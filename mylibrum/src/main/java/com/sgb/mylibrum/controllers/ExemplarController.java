package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.ExemplarRequestDTO;
import com.sgb.mylibrum.dtos.response.ExemplarResponseDTO;
import com.sgb.mylibrum.services.ExemplarService;
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

import com.sgb.mylibrum.entities.enums.StatusExemplar;
import com.sgb.mylibrum.specifications.ExemplarFiltro;
import com.sgb.mylibrum.specifications.FiltrosUtil;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exemplares")
@RequiredArgsConstructor
@Tag(name = "Exemplares", description = "Exemplares físicos dos materiais, com filtros por estado, localização, código e data de aquisição. Consultas exigem autenticação; criação e atualização permitem ADMIN, BIBLIOTECARIA e ASSISTENTE; exclusão permite ADMIN e BIBLIOTECARIA.")
@SecurityRequirement(name = "bearerAuth")
public class ExemplarController {

    private final ExemplarService service;

    @GetMapping
    @Operation(summary = "Pesquisar exemplares", description = "Lista exemplares aplicando os filtros opcionais combinados. Datas usam o formato ISO `yyyy-MM-dd`. `ativo` aceita `true`, `false` ou `todos`; se omitido, retorna apenas exemplares ativos.")
    @ApiResponse(responseCode = "200", description = "Lista de exemplares que correspondem aos filtros.")
    public ResponseEntity<List<ExemplarResponseDTO>> findAll(
            @Parameter(description = "Situação do exemplar.", example = "DISPONIVEL")
            @RequestParam(required = false) StatusExemplar status,
            @Parameter(description = "Identificador do material.")
            @RequestParam(required = false) Long materialId,
            @Parameter(description = "Identificador da filial.")
            @RequestParam(required = false) Long filialId,
            @Parameter(description = "Identificador da estante.")
            @RequestParam(required = false) Long estanteId,
            @Parameter(description = "Código de barras do exemplar.")
            @RequestParam(required = false) String codigoBarras,
            @Parameter(description = "Data inicial de aquisição (inclusiva), no formato ISO yyyy-MM-dd.", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAquisicaoDe,
            @Parameter(description = "Data final de aquisição (inclusiva), no formato ISO yyyy-MM-dd.", example = "2024-12-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataAquisicaoAte,
            @Parameter(description = "Use true para ativos, false para inativos ou todos para não filtrar; omitido, retorna apenas ativos.", example = "todos")
            @RequestParam(required = false) String ativo) {
        ExemplarFiltro filtro = new ExemplarFiltro(status, materialId, filialId, estanteId,
                FiltrosUtil.textoOuNulo(codigoBarras), dataAquisicaoDe, dataAquisicaoAte,
                FiltrosUtil.interpretarAtivo(ativo));
        return ResponseEntity.ok(service.findAll(filtro));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar exemplar", description = "Busca um exemplar pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Exemplar encontrado.")
    public ResponseEntity<ExemplarResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar exemplar", description = "Registra um exemplar para um material, filial e estante. Material, filial, estante e código de barras são obrigatórios.")
    @ApiResponse(responseCode = "201", description = "Exemplar cadastrado.")
    public ResponseEntity<ExemplarResponseDTO> create(@Valid @RequestBody ExemplarRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar exemplar", description = "Atualiza os dados do exemplar identificado.")
    @ApiResponse(responseCode = "200", description = "Exemplar atualizado.")
    public ResponseEntity<ExemplarResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ExemplarRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir exemplar", description = "Remove o exemplar identificado, sujeita às restrições de empréstimos e integridade.")
    @ApiResponse(responseCode = "204", description = "Exemplar excluído.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/estante")
    public ResponseEntity<Void> detachFromEstante(@PathVariable Long id) {
        service.detachFromEstante(id);
        return ResponseEntity.noContent().build();
    }
}