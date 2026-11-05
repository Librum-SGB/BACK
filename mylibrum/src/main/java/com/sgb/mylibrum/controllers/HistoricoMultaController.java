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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/historico-multas")
@RequiredArgsConstructor
@Tag(name = "Histórico de multas", description = "Consulta e gerenciamento de multas vinculadas a empréstimos. Consultas individuais respeitam a regra de próprio usuário ou equipe.")
@SecurityRequirement(name = "bearerAuth")
public class HistoricoMultaController {

    private final HistoricoMultaService service;
    private final AutorizacaoService autorizacao;

    @GetMapping
    @Operation(summary = "Listar multas", description = "Lista multas com filtros opcionais por status e situação ativa. Listagem geral restrita a equipe autorizada.")
    @ApiResponse(responseCode = "200", description = "Lista de multas.")
    public ResponseEntity<List<HistoricoMultaResponseDTO>> findAll(
            @Parameter(description = "Status da multa.", example = "PENDENTE")
            @RequestParam(required = false) StatusMulta status,
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(status, ativo));
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Listar multas de usuário", description = "Lista multas vinculadas ao usuário indicado. Usuários podem consultar somente as próprias multas; equipe autorizada pode consultar outras.")
    @ApiResponse(responseCode = "200", description = "Lista de multas do usuário.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem permissão para consultar este usuário.")
    public ResponseEntity<List<HistoricoMultaResponseDTO>> findByUsuarioId(
            @Parameter(description = "Identificador do usuário.")
            @PathVariable Long id,
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        autorizacao.exigirProprioOuEquipe(id);
        return ResponseEntity.ok(service.findByUsuarioId(id, ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar multa", description = "Busca uma multa por identificador. O acesso é permitido ao próprio usuário associado ou à equipe autorizada.")
    @ApiResponse(responseCode = "200", description = "Multa encontrada.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem permissão para consultar esta multa.")
    public ResponseEntity<HistoricoMultaResponseDTO> findById(@PathVariable Long id) {
            HistoricoMultaResponseDTO multa = service.findById(id);
            autorizacao.exigirProprioOuEquipe(multa.getUsuarioId());
            return ResponseEntity.ok(multa);
    }

    @PostMapping
    @Operation(summary = "Registrar multa", description = "Cria um registro de multa para um empréstimo, com valor e dias de atraso informados.")
    @ApiResponse(responseCode = "201", description = "Multa registrada.")
    public ResponseEntity<HistoricoMultaResponseDTO> create(@Valid @RequestBody HistoricoMultaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PatchMapping("/{id}/pagamento")
    @Operation(summary = "Registrar pagamento de multa", description = "Registra o pagamento da multa identificada. Requer perfil administrativo, bibliotecário ou assistente.")
    @ApiResponse(responseCode = "200", description = "Pagamento registrado e multa atualizada.")
    public ResponseEntity<HistoricoMultaResponseDTO> registrarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(service.registrarPagamento(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar multa", description = "Atualiza os dados da multa identificada. Requer perfil administrativo ou de bibliotecário.")
    @ApiResponse(responseCode = "200", description = "Multa atualizada.")
    public ResponseEntity<HistoricoMultaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody HistoricoMultaRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir multa", description = "Remove a multa identificada. Requer perfil administrativo ou de bibliotecário.")
    @ApiResponse(responseCode = "204", description = "Multa excluída.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}