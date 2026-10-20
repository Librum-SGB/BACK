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

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
public class ListaTarefaController {

    private final ListaTarefaService service;
    private final AutorizacaoService autorizacao;

    @GetMapping
    public ResponseEntity<List<ListaTarefaResponseDTO>> findAll(
            @RequestParam(required = false) Boolean ativo) {
        if (autorizacao.funcaoLogada() == FuncaoUsuario.USUARIO) {
            return ResponseEntity.ok(service.findByGestorId(autorizacao.idLogado()));
        }
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/gestores/{id}")
    public ResponseEntity<List<ListaTarefaResponseDTO>> findByGestorId(@PathVariable Long id) {
        autorizacao.exigirProprioOuEquipe(id);
        return ResponseEntity.ok(service.findByGestorId(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaTarefaResponseDTO> findById(@PathVariable Long id) {
        ListaTarefaResponseDTO tarefa = service.findById(id);
        autorizacao.exigirProprioOuEquipe(tarefa.getUsuarioId());
        return ResponseEntity.ok(tarefa);
    }

    @PostMapping
    public ResponseEntity<ListaTarefaResponseDTO> create(@Valid @RequestBody ListaTarefaRequestDTO dto) {
        dto.setUsuarioId(autorizacao.idLogado());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListaTarefaResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ListaTarefaRequestDTO dto) {
        autorizacao.exigirProprio(service.findById(id).getUsuarioId());
        dto.setUsuarioId(autorizacao.idLogado());
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        autorizacao.exigirProprio(service.findById(id).getUsuarioId());
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}