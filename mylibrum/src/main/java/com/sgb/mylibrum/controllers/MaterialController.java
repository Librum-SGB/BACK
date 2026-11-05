package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.services.MaterialService;
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

import com.sgb.mylibrum.entities.enums.TipoMaterial;
import com.sgb.mylibrum.specifications.FiltrosUtil;
import com.sgb.mylibrum.specifications.MaterialFiltro;

import java.util.List;

@RestController
@RequestMapping("/api/materiais")
@RequiredArgsConstructor
@Tag(name = "Materiais", description = "Catálogo bibliográfico da biblioteca, com filtros por identificação, tipo, autoria, gênero, editora e ano. Consultas exigem autenticação; criação e atualização permitem ADMIN, BIBLIOTECARIA e ASSISTENTE; exclusão permite ADMIN e BIBLIOTECARIA.")
@SecurityRequirement(name = "bearerAuth")
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    @Operation(summary = "Cadastrar material", description = "Cria um material no catálogo. Título e editora são obrigatórios; autores e gêneros podem ser associados por identificador.")
    @ApiResponse(responseCode = "201", description = "Material criado.")
    @ApiResponse(responseCode = "204", description = "Nenhum material foi criado.")
    public ResponseEntity<MaterialResponseDTO> criar(
            @Valid @RequestBody MaterialRequestDTO materialRequestDTO) {

        MaterialResponseDTO salvo = materialService.create(materialRequestDTO);

        if (salvo == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    @Operation(summary = "Pesquisar materiais", description = "Pesquisa materiais combinando os filtros opcionais. Ano inicial e final delimitam o intervalo de publicação. `ativo` aceita `true`, `false` ou `todos`; se omitido, retorna apenas materiais ativos.")
    @ApiResponse(responseCode = "200", description = "Lista de materiais encontrados.")
    @ApiResponse(responseCode = "204", description = "Nenhum material corresponde aos filtros.")
    public ResponseEntity<List<MaterialResponseDTO>> listarTodos(
            @Parameter(description = "Parte do título a pesquisar.")
            @RequestParam(required = false) String titulo,
            @Parameter(description = "ISBN do material.")
            @RequestParam(required = false) String isbn,
            @Parameter(description = "Tipo de material.", example = "LIVRO")
            @RequestParam(required = false) TipoMaterial tipo,
            @Parameter(description = "Identificador da editora.")
            @RequestParam(required = false) Long editoraId,
            @Parameter(description = "Identificador do autor.")
            @RequestParam(required = false) Long autorId,
            @Parameter(description = "Identificador do gênero.")
            @RequestParam(required = false) Long generoId,
            @Parameter(description = "Ano inicial de publicação (inclusivo).", example = "2000")
            @RequestParam(required = false) Integer anoPublicacaoDe,
            @Parameter(description = "Ano final de publicação (inclusivo).", example = "2024")
            @RequestParam(required = false) Integer anoPublicacaoAte,
            @Parameter(description = "Parte do tema a pesquisar.")
            @RequestParam(required = false) String tema,
            @Parameter(description = "Use true para ativos, false para inativos ou todos para não filtrar; omitido, retorna apenas ativos.", example = "todos")
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
    @Operation(summary = "Consultar material", description = "Busca um material pelo identificador.")
    @ApiResponse(responseCode = "200", description = "Material encontrado.")
    public ResponseEntity<MaterialResponseDTO> buscarPorId(
            @Parameter(description = "Identificador do material.")
            @PathVariable Long id) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(materialService.findById(id));
    }

    @GetMapping("/isbn")
    @Operation(summary = "Buscar material por ISBN", description = "Localiza um material pelo ISBN exato.")
    @ApiResponse(responseCode = "200", description = "Material encontrado.")
    @ApiResponse(responseCode = "204", description = "Nenhum material encontrado para o ISBN.")
    public ResponseEntity<MaterialResponseDTO> buscarPorIsbn(
            @Parameter(description = "ISBN a consultar.", example = "9788535902778")
            @RequestParam String isbn) {

        MaterialResponseDTO material = materialService.findByIsbn(isbn);

        if (material == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(material);
    }

    @GetMapping("/titulo")
    @Operation(summary = "Buscar materiais por título", description = "Pesquisa materiais pelo título e, opcionalmente, pela situação.")
    @ApiResponse(responseCode = "200", description = "Lista de materiais encontrados.")
    @ApiResponse(responseCode = "204", description = "Nenhum material encontrado.")
    public ResponseEntity<List<MaterialResponseDTO>> buscarPorTitulo(
            @Parameter(description = "Título ou trecho do título a pesquisar.")
            @RequestParam String titulo,
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {

        List<MaterialResponseDTO> materiais =
                materialService.findByTitulo(titulo, ativo);

        if (materiais.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(materiais);
    }

    @GetMapping("/editora")
    @Operation(summary = "Listar materiais de uma editora", description = "Retorna materiais vinculados à editora informada, com filtro opcional por situação.")
    @ApiResponse(responseCode = "200", description = "Lista de materiais da editora.")
    @ApiResponse(responseCode = "204", description = "Nenhum material encontrado para a editora.")
    public ResponseEntity<List<MaterialResponseDTO>> buscarPorEditora(
            @Parameter(description = "Identificador da editora.")
            @RequestParam Long editoraId,
            @Parameter(description = "Filtra por situação ativa (true) ou inativa (false).")
            @RequestParam(required = false) Boolean ativo) {

        List<MaterialResponseDTO> materiais =
                materialService.findByEditoraId(editoraId, ativo);

        if (materiais.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(materiais);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar material", description = "Atualiza o material identificado. Título e editora são obrigatórios.")
    @ApiResponse(responseCode = "200", description = "Material atualizado.")
    @ApiResponse(responseCode = "204", description = "Nenhum material foi atualizado.")
    public ResponseEntity<MaterialResponseDTO> alterar(
            @Parameter(description = "Identificador do material.")
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
    @Operation(summary = "Excluir material", description = "Remove o material identificado, sujeita às restrições de exemplares e empréstimos.")
    @ApiResponse(responseCode = "204", description = "Material excluído.")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        materialService.delete(id);

        return ResponseEntity.noContent().build();
    }
}