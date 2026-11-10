package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.ListaTarefaRequestDTO;
import com.sgb.mylibrum.dtos.response.ListaTarefaResponseDTO;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.security.AutorizacaoService;
import com.sgb.mylibrum.services.ListaTarefaService;
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
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Tarefas associadas a usuários. Usuários comuns veem e alteram somente suas próprias tarefas.")
@SecurityRequirement(name = "bearerAuth")
public class ListaTarefaController {

    private final ListaTarefaService service;
    private final AutorizacaoService autorizacao;

    @GetMapping
    @Operation(summary = "Listar tarefas", description = "Lista tarefas. Usuários comuns recebem apenas as próprias; perfis da equipe podem consultar a lista filtrada por situação.")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas visível ao usuário autenticado.")
    public ResponseEntity<List<ListaTarefaResponseDTO>> findAll(
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false); aplicado à listagem da equipe.")
            @RequestParam(required = false) Boolean ativo) {
        if (autorizacao.funcaoLogada() == FuncaoUsuario.USUARIO) {
            return ResponseEntity.ok(service.findByGestorId(autorizacao.idLogado()));
        }
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/minhas")
    @Operation(summary = "Listar minhas tarefas", description = "Lista somente as tarefas do usuário autenticado.")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas do usuário autenticado.")
    public ResponseEntity<List<ListaTarefaResponseDTO>> findMine() {
        return ResponseEntity.ok(service.findByGestorId(autorizacao.idLogado()));
    }

    @GetMapping("/gestores/{id}")
    @Operation(summary = "Listar tarefas de usuário", description = "Lista tarefas vinculadas ao usuário indicado. O usuário autenticado só pode consultar a si próprio, salvo equipe autorizada.")
    @ApiResponse(responseCode = "200", description = "Lista de tarefas do usuário.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem permissão para consultar este usuário.")
    public ResponseEntity<List<ListaTarefaResponseDTO>> findByGestorId(@PathVariable Long id) {
        autorizacao.exigirProprioOuEquipe(id);
        return ResponseEntity.ok(service.findByGestorId(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar tarefa", description = "Busca uma tarefa por identificador; requer que o usuário autenticado seja o proprietário ou membro autorizado da equipe.")
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem permissão para consultar esta tarefa.")
    public ResponseEntity<ListaTarefaResponseDTO> findById(@PathVariable Long id) {
        ListaTarefaResponseDTO tarefa = service.findById(id);
        autorizacao.exigirProprioOuEquipe(tarefa.getUsuarioId());
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    @Operation(summary = "Criar tarefa", description = "Cria uma tarefa para o usuário autenticado. O `usuarioId` enviado no corpo é substituído pelo identificador do usuário autenticado.")
    @ApiResponse(responseCode = "201", description = "Tarefa criada.")
    public ResponseEntity<ListaTarefaResponseDTO> create(@Valid @RequestBody ListaTarefaRequestDTO dto) {
        dto.setUsuarioId(autorizacao.idLogado());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa", description = "Atualiza uma tarefa existente. Somente o proprietário pode alterá-la; o usuário associado é definido pela autenticação.")
    @ApiResponse(responseCode = "200", description = "Tarefa atualizada.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado não é proprietário da tarefa.")
    public ResponseEntity<ListaTarefaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ListaTarefaRequestDTO dto) {
        autorizacao.exigirProprio(service.findById(id).getUsuarioId());
        dto.setUsuarioId(autorizacao.idLogado());
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir tarefa", description = "Remove uma tarefa existente. Somente o proprietário pode excluí-la.")
    @ApiResponse(responseCode = "204", description = "Tarefa excluída.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado não é proprietário da tarefa.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        autorizacao.exigirProprio(service.findById(id).getUsuarioId());
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}