package com.sgb.mylibrum.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sgb.mylibrum.dtos.request.CadastroIsbnRequestDTO;
import com.sgb.mylibrum.dtos.response.CadastroIsbnResponseDTO;
import com.sgb.mylibrum.dtos.response.ConsultaIsbnResponseDTO;
import com.sgb.mylibrum.services.MaterialIsbnService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/materiais/isbn")
@RequiredArgsConstructor
public class MaterialIsbnController {

    private final MaterialIsbnService materialIsbnService;

    @GetMapping("/consulta")
    public ResponseEntity<ConsultaIsbnResponseDTO> consultar(@RequestParam String isbn) {
        return ResponseEntity.ok(materialIsbnService.consultar(isbn));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<CadastroIsbnResponseDTO> cadastrar(@Valid @RequestBody CadastroIsbnRequestDTO request) {
        CadastroIsbnResponseDTO resposta = materialIsbnService.cadastrar(request);
        return ResponseEntity.status(resposta.criado() ? HttpStatus.CREATED : HttpStatus.OK).body(resposta);
    }
}
