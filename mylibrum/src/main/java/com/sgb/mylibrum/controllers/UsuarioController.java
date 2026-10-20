package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.request.BloqueioUsuarioRequestDTO;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.security.AutorizacaoService;
import com.sgb.mylibrum.services.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final AutorizacaoService autorizacao;

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDTO>> findAll(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String matriculaFuncionario,
            @RequestParam(required = false) Boolean ativo,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.findAll(nome, matriculaFuncionario, ativo, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        autorizacao.exigirProprioOuEquipe(id);
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO dto) {
        autorizacao.exigirPodeAtribuirFuncao(dto.getFuncao() == null ? FuncaoUsuario.USUARIO : dto.getFuncao());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO atual = service.findById(id);
        FuncaoUsuario logada = autorizacao.funcaoLogada();
        if (logada == FuncaoUsuario.USUARIO) {
            autorizacao.exigirProprio(id);
            dto.setFuncao(atual.getFuncao());
            dto.setEstaBloqueado(atual.getEstaBloqueado());
            dto.setFilialId(atual.getFilialId());
            dto.setLimiteLivros(atual.getLimiteLivros());
            dto.setMatriculaFuncionario(atual.getMatriculaFuncionario());
        } else if (logada != FuncaoUsuario.ADMIN) {
            autorizacao.exigirPodeGerenciarConta(id, atual.getFuncao(), true);
            if (dto.getFuncao() == null) {
                dto.setFuncao(atual.getFuncao());
            } else if (dto.getFuncao() != atual.getFuncao()) {
                autorizacao.exigirPodeAtribuirFuncao(dto.getFuncao());
            }
        }
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}/bloqueio")
    public ResponseEntity<UsuarioResponseDTO> atualizarBloqueio(
            @PathVariable Long id, @Valid @RequestBody BloqueioUsuarioRequestDTO dto) {
        autorizacao.exigirPodeGerenciarConta(id, service.findById(id).getFuncao(), false);
        return ResponseEntity.ok(service.atualizarBloqueio(id, dto.getBloqueado()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        autorizacao.exigirPodeGerenciarConta(id, service.findById(id).getFuncao(), false);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}