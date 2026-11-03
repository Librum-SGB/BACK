package com.sgb.mylibrum.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Autenticação", description = "Autenticação pública e obtenção de token de acesso.")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Valida e-mail e senha e retorna um JWT Bearer e a função do usuário. Use o token no botão Authorize do Swagger para acessar endpoints protegidos.")
    @ApiResponse(responseCode = "200", description = "Autenticação concluída. A resposta contém `token`, `type` (Bearer), `funcao` e `message`.")
    @ApiResponse(responseCode = "401", description = "Credenciais inválidas.", content = @io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = com.sgb.mylibrum.exceptions.ApiError.class)))
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas"));

        if (usuario.getSenha() == null || !passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
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

    @Data
    @Schema(name = "LoginRequest", description = "Credenciais necessárias para autenticação.")
    public static class LoginRequest {
        @jakarta.validation.constraints.NotBlank(message = "O e-mail é obrigatório")
        @jakarta.validation.constraints.Email(message = "O e-mail deve ser válido")
        @Schema(description = "E-mail cadastrado do usuário.", example = "usuario@biblioteca.com", requiredMode = Schema.RequiredMode.REQUIRED)
        private String email;

        @jakarta.validation.constraints.NotBlank(message = "A senha é obrigatória")
        @Schema(description = "Senha cadastrada. Nunca é retornada pela API.", example = "SenhaSegura123", requiredMode = Schema.RequiredMode.REQUIRED)
        private String senha;
    }
}
