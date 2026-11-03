package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.EmprestimoRequestDTO;
import com.sgb.mylibrum.dtos.response.EmprestimoResponseDTO;
import com.sgb.mylibrum.security.AutorizacaoService;
import com.sgb.mylibrum.services.EmprestimoService;
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
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "Consulta e gerenciamento de empréstimos. Consultas individuais por usuário respeitam a regra de próprio usuário ou equipe.")
@SecurityRequirement(name = "bearerAuth")
public class EmprestimoController {

    private final EmprestimoService service;
    private final AutorizacaoService autorizacao;

    @GetMapping
    @Operation(summary = "Listar empréstimos", description = "Lista empréstimos; acesso à listagem geral é restrito a administradores, bibliotecários e assistentes.")
    @ApiResponse(responseCode = "200", description = "Lista de empréstimos.")
    public ResponseEntity<List<EmprestimoResponseDTO>> findAll(
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(service.findAll(ativo));
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Listar empréstimos de usuário", description = "Lista empréstimos do usuário indicado. Usuários podem consultar somente os próprios dados; equipe autorizada pode consultar outros usuários.")
    @ApiResponse(responseCode = "200", description = "Lista de empréstimos do usuário.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem permissão para consultar este usuário.")
    public ResponseEntity<List<EmprestimoResponseDTO>> findByUsuarioId(
            @Parameter(description = "Identificador do usuário.")
            @PathVariable Long id,
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {
        autorizacao.exigirProprioOuEquipe(id);
        return ResponseEntity.ok(service.findByUsuarioId(id, ativo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar empréstimo", description = "Busca um empréstimo por identificador. O acesso é permitido ao próprio usuário do empréstimo ou à equipe autorizada.")
    @ApiResponse(responseCode = "200", description = "Empréstimo encontrado.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem permissão para consultar este empréstimo.")
    public ResponseEntity<EmprestimoResponseDTO> findById(@PathVariable Long id) {
        EmprestimoResponseDTO emprestimo = service.findById(id);
        autorizacao.exigirProprioOuEquipe(emprestimo.getUsuarioId());
        return ResponseEntity.ok(emprestimo);
    }

    @PostMapping
    @Operation(summary = "Registrar empréstimo", description = "Cria um empréstimo para o usuário e exemplar informados. A criação é permitida à equipe com perfil autorizado.")
    @ApiResponse(responseCode = "201", description = "Empréstimo criado.")
    public ResponseEntity<EmprestimoResponseDTO> create(@Valid @RequestBody EmprestimoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PostMapping("/{id}/devolucao")
    @Operation(summary = "Registrar devolução", description = "Registra a devolução do empréstimo indicado e retorna o empréstimo atualizado.")
    @ApiResponse(responseCode = "200", description = "Devolução registrada.")
    public ResponseEntity<EmprestimoResponseDTO> devolver(@PathVariable Long id) {
        return ResponseEntity.ok(service.devolver(id));
    }

    @PostMapping("/{id}/renovacao")
    @Operation(summary = "Renovar empréstimo", description = "Solicita a renovação do empréstimo indicado conforme as regras de negócio.")
    @ApiResponse(responseCode = "200", description = "Empréstimo renovado.")
    public ResponseEntity<EmprestimoResponseDTO> renovar(@PathVariable Long id) {
        return ResponseEntity.ok(service.renovar(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar empréstimo", description = "Atualiza o empréstimo indicado. Requer perfil administrativo ou de bibliotecário.")
    @ApiResponse(responseCode = "200", description = "Empréstimo atualizado.")
    public ResponseEntity<EmprestimoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EmprestimoRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir empréstimo", description = "Remove o empréstimo indicado. Requer perfil administrativo ou de bibliotecário.")
    @ApiResponse(responseCode = "204", description = "Empréstimo excluído.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}