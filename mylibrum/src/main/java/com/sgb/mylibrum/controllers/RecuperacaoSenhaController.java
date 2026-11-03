package com.sgb.mylibrum.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.sgb.mylibrum.dtos.request.EsqueciSenhaRequestDTO;
import com.sgb.mylibrum.dtos.request.RedefinirSenhaRequestDTO;
import com.sgb.mylibrum.services.RecuperacaoSenhaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Recuperação de senha", description = "Solicitação e conclusão do fluxo de recuperação de senha. Endpoints públicos.")
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService service;

    @PostMapping("/esqueci-senha")
    @Operation(summary = "Solicitar recuperação de senha", description = "Solicita o envio de instruções ao e-mail informado. A resposta é deliberadamente genérica para não revelar se o endereço está cadastrado.")
    @ApiResponse(responseCode = "200", description = "Solicitação recebida; a mensagem não confirma a existência do e-mail.")
    public ResponseEntity<Map<String, String>> esqueciSenha(@Valid @RequestBody EsqueciSenhaRequestDTO dto) {
        service.solicitar(dto.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Se o e-mail estiver cadastrado, enviaremos as instruções de recuperação de senha."));
    }

    @PostMapping("/redefinir-senha")
    @Operation(summary = "Redefinir senha", description = "Redefine a senha usando o token de recuperação. A nova senha deve ter entre 6 e 72 caracteres.")
    @ApiResponse(responseCode = "200", description = "Senha redefinida.")
    @ApiResponse(responseCode = "400", description = "Token inválido, expirado ou dados inválidos.")
    public ResponseEntity<Map<String, String>> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequestDTO dto) {
        service.redefinir(dto.getToken(), dto.getNovaSenha());
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso"));
    }
}
