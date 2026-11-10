package com.sgb.mylibrum.controllers;

import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.dtos.request.BloqueioUsuarioRequestDTO;
import com.sgb.mylibrum.dtos.response.UsuarioResponseDTO;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.security.AutorizacaoService;
import com.sgb.mylibrum.services.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.sgb.mylibrum.specifications.FiltrosUtil;
import com.sgb.mylibrum.specifications.UsuarioFiltro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Contas, perfis, busca paginada e bloqueio. A visibilidade e as alterações dependem do próprio usuário e da hierarquia de perfis.")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService service;
    private final AutorizacaoService autorizacao;

    @GetMapping
    @Operation(summary = "Pesquisar usuários", description = "Pesquisa usuários com filtros combináveis e paginação. A consulta geral é restrita a administradores, bibliotecários e assistentes.")
    @ApiResponse(responseCode = "200", description = "Página de usuários correspondente aos filtros.")
    public ResponseEntity<Page<UsuarioResponseDTO>> findAll(
            @Parameter(description = "Filtra por nome.")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Filtra pela matrícula do funcionário.")
            @RequestParam(required = false) String matriculaFuncionario,
            @Parameter(description = "Filtra pelo e-mail.")
            @RequestParam(required = false) String email,
            @Parameter(description = "Filtra pelo CPF.")
            @RequestParam(required = false) String cpf,
            @Parameter(description = "Filtra pela função do usuário.", example = "USUARIO")
            @RequestParam(required = false) FuncaoUsuario funcao,
            @Parameter(description = "Filtra pelo identificador da filial.")
            @RequestParam(required = false) Long filialId,
            @Parameter(description = "Filtra pelo estado de bloqueio.")
            @RequestParam(required = false) Boolean estaBloqueado,
            @Parameter(description = "Filtra pela cidade.")
            @RequestParam(required = false) String cidade,
            @Parameter(description = "Use true para ativos, false para inativos ou todos para não filtrar; omitido, retorna apenas ativos.", example = "todos")
            @RequestParam(required = false) String ativo,
            @ParameterObject
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        UsuarioFiltro filtro = new UsuarioFiltro(nome, matriculaFuncionario, email, cpf, funcao, filialId,
                estaBloqueado, cidade, FiltrosUtil.interpretarAtivo(ativo));
        return ResponseEntity.ok(service.findAll(filtro, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuário", description = "Busca um usuário pelo identificador. O próprio usuário ou equipe autorizada pode acessar conforme as regras de autorização.")
    @ApiResponse(responseCode = "200", description = "Usuário encontrado.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem permissão para consultar este perfil.")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        autorizacao.exigirProprioOuEquipe(id);
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar usuário", description = "Cria uma conta. Nome, e-mail, senha e filial são obrigatórios; a função atribuída depende do perfil autenticado.")
    @ApiResponse(responseCode = "201", description = "Usuário criado.")
    @ApiResponse(responseCode = "403", description = "Perfil autenticado não pode atribuir a função solicitada.")
    public ResponseEntity<UsuarioResponseDTO> create(
            @Validated({ Default.class, UsuarioRequestDTO.Criacao.class }) @RequestBody UsuarioRequestDTO dto) {
        autorizacao.exigirPodeAtribuirFuncao(dto.getFuncao() == null ? FuncaoUsuario.USUARIO : dto.getFuncao());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza o perfil indicado respeitando regras hierárquicas. Um usuário comum só altera seus próprios dados e não pode mudar função, bloqueio, filial, limite ou matrícula.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado.")
    @ApiResponse(responseCode = "403", description = "Usuário autenticado sem permissão para alterar este perfil ou função.")
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
    @Operation(summary = "Atualizar bloqueio", description = "Altera apenas o estado de bloqueio da conta. Requer permissão de gerenciamento sobre o usuário.")
    @ApiResponse(responseCode = "200", description = "Estado de bloqueio atualizado.")
    @ApiResponse(responseCode = "403", description = "Perfil autenticado sem permissão para gerenciar esta conta.")
    public ResponseEntity<UsuarioResponseDTO> atualizarBloqueio(
            @PathVariable Long id, @Valid @RequestBody BloqueioUsuarioRequestDTO dto) {
        autorizacao.exigirPodeGerenciarConta(id, service.findById(id).getFuncao(), false);
        return ResponseEntity.ok(service.atualizarBloqueio(id, dto.getBloqueado()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir usuário", description = "Remove o usuário indicado. Requer permissão de gerenciamento sobre a conta.")
    @ApiResponse(responseCode = "204", description = "Usuário excluído.")
    @ApiResponse(responseCode = "403", description = "Perfil autenticado sem permissão para gerenciar esta conta.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        autorizacao.exigirPodeGerenciarConta(id, service.findById(id).getFuncao(), false);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}