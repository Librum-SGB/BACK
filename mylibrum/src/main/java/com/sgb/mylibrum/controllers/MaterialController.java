package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.services.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sgb.mylibrum.entities.enums.TipoMaterial;
import com.sgb.mylibrum.specifications.FiltrosUtil;
import com.sgb.mylibrum.specifications.MaterialFiltro;

import java.util.List;

@RestController
@RequestMapping("/api/materiais")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    public ResponseEntity<MaterialResponseDTO> criar(
            @Valid @RequestBody MaterialRequestDTO materialRequestDTO) {

        MaterialResponseDTO salvo = materialService.create(materialRequestDTO);

        if (salvo == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<MaterialResponseDTO>> listarTodos(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) TipoMaterial tipo,
            @RequestParam(required = false) Long editoraId,
            @RequestParam(required = false) Long autorId,
            @RequestParam(required = false) Long generoId,
            @RequestParam(required = false) Integer anoPublicacaoDe,
            @RequestParam(required = false) Integer anoPublicacaoAte,
            @RequestParam(required = false) String tema,
            @RequestParam(required = false) String ativo) {

        MaterialFiltro filtro = new MaterialFiltro(FiltrosUtil.textoOuNulo(titulo), FiltrosUtil.textoOuNulo(isbn),
                tipo, editoraId, autorId, generoId, anoPublicacaoDe, anoPublicacaoAte,
                FiltrosUtil.textoOuNulo(tema), FiltrosUtil.interpretarAtivo(ativo));
        List<MaterialResponseDTO> materiais = materialService.findAll(filtro);

        if (materiais.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(materiais);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(materialService.findById(id));
    }

    @GetMapping("/isbn")
    public ResponseEntity<MaterialResponseDTO> buscarPorIsbn(
            @RequestParam String isbn) {

        MaterialResponseDTO material = materialService.findByIsbn(isbn);

        if (material == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(material);
    }

    @GetMapping("/titulo")
    public ResponseEntity<List<MaterialResponseDTO>> buscarPorTitulo(
            @RequestParam String titulo,
            @RequestParam(required = false) Boolean ativo) {

        List<MaterialResponseDTO> materiais =
                materialService.findByTitulo(titulo, ativo);

        if (materiais.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(materiais);
    }

    @GetMapping("/editora")
    public ResponseEntity<List<MaterialResponseDTO>> buscarPorEditora(
            @RequestParam Long editoraId,
            @RequestParam(required = false) Boolean ativo) {

        List<MaterialResponseDTO> materiais =
                materialService.findByEditoraId(editoraId, ativo);

        if (materiais.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(materiais);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialResponseDTO> alterar(
            @PathVariable Long id,
            @Valid @RequestBody MaterialRequestDTO materialRequestDTO) {

        MaterialResponseDTO modificado =
                materialService.update(id, materialRequestDTO);

        if (modificado == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(modificado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        materialService.delete(id);

        return ResponseEntity.noContent().build();
    }
}