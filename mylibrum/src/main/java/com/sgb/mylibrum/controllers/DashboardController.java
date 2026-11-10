package com.sgb.mylibrum.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sgb.mylibrum.dtos.response.DashboardResumoResponseDTO;
import com.sgb.mylibrum.services.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Indicadores atuais do acervo, usuários e empréstimos.")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/resumo")
    @Operation(summary = "Consultar resumo do dashboard", description = "Retorna contagens calculadas a partir do banco e os empréstimos ativos mais próximos do vencimento.")
    @ApiResponse(responseCode = "200", description = "Resumo atualizado do dashboard.")
    public ResponseEntity<DashboardResumoResponseDTO> obterResumo() {
        return ResponseEntity.ok(dashboardService.obterResumo());
    }
}
