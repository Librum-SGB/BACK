package com.sgb.mylibrum.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sgb.mylibrum.dtos.request.EsqueciSenhaRequestDTO;
import com.sgb.mylibrum.dtos.request.RedefinirSenhaRequestDTO;
import com.sgb.mylibrum.services.RecuperacaoSenhaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RecuperacaoSenhaController {

    private final RecuperacaoSenhaService service;

    @PostMapping("/esqueci-senha")
    public ResponseEntity<Map<String, String>> esqueciSenha(@Valid @RequestBody EsqueciSenhaRequestDTO dto) {
        service.solicitar(dto.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Se o e-mail estiver cadastrado, enviaremos as instruções de recuperação de senha."));
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequestDTO dto) {
        service.redefinir(dto.getToken(), dto.getNovaSenha());
        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso"));
    }
}
