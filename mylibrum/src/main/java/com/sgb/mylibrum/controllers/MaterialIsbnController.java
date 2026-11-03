package com.sgb.mylibrum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.sgb.mylibrum.dtos.request.CadastroIsbnRequestDTO;
import com.sgb.mylibrum.dtos.response.CadastroIsbnResponseDTO;
import com.sgb.mylibrum.dtos.response.ConsultaIsbnResponseDTO;
import com.sgb.mylibrum.services.MaterialIsbnService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/materiais/isbn")
@RequiredArgsConstructor
@Tag(name = "Integração ISBN", description = "Consulta dados bibliográficos por ISBN e permite cadastrar o material com dados externos ou informados. Acesso restrito a ADMIN e BIBLIOTECARIA.")
@SecurityRequirement(name = "bearerAuth")
public class MaterialIsbnController {

    private final MaterialIsbnService materialIsbnService;

    @GetMapping("/consulta")
    @Operation(summary = "Consultar ISBN", description = "Busca os dados associados ao ISBN sem gravar alterações. Indica se já existe no acervo e lista autores/gêneros que seriam criados.")
    @ApiResponse(responseCode = "200", description = "Prévia local e/ou externa da consulta.")
    public ResponseEntity<ConsultaIsbnResponseDTO> consultar(@RequestParam String isbn) {
        return ResponseEntity.ok(materialIsbnService.consultar(isbn));
    }

    @PostMapping("/cadastro")
    @Operation(summary = "Cadastrar material por ISBN", description = "Cadastra material a partir do ISBN. O ISBN é obrigatório; campos informados substituem dados externos. Retorna 201 se criado ou 200 se já existia.")
    @ApiResponse(responseCode = "201", description = "Material criado a partir do ISBN.")
    @ApiResponse(responseCode = "200", description = "O material já estava cadastrado; retorna o registro existente.")
    public ResponseEntity<CadastroIsbnResponseDTO> cadastrar(@Valid @RequestBody CadastroIsbnRequestDTO request) {
        CadastroIsbnResponseDTO resposta = materialIsbnService.cadastrar(request);
        return ResponseEntity.status(resposta.criado() ? HttpStatus.CREATED : HttpStatus.OK).body(resposta);
    }
}
