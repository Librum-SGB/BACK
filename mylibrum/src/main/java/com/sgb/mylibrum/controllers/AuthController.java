package com.sgb.mylibrum.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import com.sgb.mylibrum.security.JwtUtil;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));

        if (usuario.getSenha() == null || !passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }

        return loginResponse(usuario.getEmail(), usuario.getFuncao());
    }

    private ResponseEntity<Map<String, String>> loginResponse(String email, FuncaoUsuario funcao) {
        String token = jwtUtil.generateToken(email, funcao);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "type", "Bearer",
                "funcao", funcao.name(),
                "message", "Login realizado com sucesso"
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", ex.getMessage()
        ));
    }

    @Data
    public static class LoginRequest {
        @jakarta.validation.constraints.NotBlank(message = "O e-mail é obrigatório")
        @jakarta.validation.constraints.Email(message = "O e-mail deve ser válido")
        private String email;

        @jakarta.validation.constraints.NotBlank(message = "A senha é obrigatória")
        private String senha;
    }
}
