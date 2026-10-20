package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.request.ListaTarefaRequestDTO;
import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.entities.ListaTarefa;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoMapperTest {

    @Test
    void listaTarefaMapperConverteStatusEPrazoEntreDtoEEntidade() {
        ListaTarefaRequestDTO request = new ListaTarefaRequestDTO();
        request.setUsuarioId(9L);
        request.setDescricao("Revisar exemplares");
        request.setStatus(StatusTarefa.EM_ANDAMENTO);
        request.setPrazo(LocalDate.of(2026, 12, 31));

        ListaTarefaMapper mapper = new ListaTarefaMapper();
        ListaTarefa entity = mapper.toEntity(request);
        var response = mapper.toResponseDTO(entity);

        assertEquals(StatusTarefa.EM_ANDAMENTO, entity.getStatus());
        assertEquals(LocalDate.of(2026, 12, 31), entity.getPrazo());
        assertEquals(StatusTarefa.EM_ANDAMENTO, response.getStatus());
        assertEquals(LocalDate.of(2026, 12, 31), response.getPrazo());
    }

    @Test
    void listaTarefaRequestUsaPendenteComoStatusPadrao() {
        assertEquals(StatusTarefa.PENDENTE, new ListaTarefaRequestDTO().getStatus());
    }

    @Test
    void configuracaoMapperMascaraSenhaSmtpENaoSobrescreveComValorMascarado() {
        Configuracao entity = new Configuracao();
        entity.setChave("smtp.senha");
        entity.setValor("segredo");

        assertEquals("********", new ConfiguracaoMapper().toResponseDTO(entity).getValor());

        ConfiguracaoRequestDTO request = new ConfiguracaoRequestDTO();
        request.setChave("smtp.senha");
        request.setValor("********");
        new ConfiguracaoMapper().updateEntity(request, entity);
        assertEquals("segredo", entity.getValor());

        request.setValor("nova-senha");
        new ConfiguracaoMapper().updateEntity(request, entity);
        assertEquals("nova-senha", entity.getValor());
    }

    @Test
    void configuracaoMapperConverteFilialEntreDtoEEntidade() {
        ConfiguracaoRequestDTO request = new ConfiguracaoRequestDTO();
        request.setFilialId(7L);
        request.setChave("limite");
        request.setValor("5");

        ConfiguracaoMapper mapper = new ConfiguracaoMapper();
        Configuracao entity = mapper.toEntity(request);

        assertEquals(7L, entity.getFilial().getId());
        assertEquals("limite", entity.getChave());
        assertEquals("5", entity.getValor());

        entity.setId(3L);
        assertEquals(7L, mapper.toResponseDTO(entity).getFilialId());
    }

    @Test
    void usuarioMapperPreservaSenhaEFuncaoQuandoAtualizacaoNaoDefineFuncao() {
        Usuario usuario = new Usuario();
        usuario.setSenha("senha-criptografada");
        usuario.setFuncao(FuncaoUsuario.ADMIN);

        UsuarioRequestDTO request = new UsuarioRequestDTO();
        request.setNome("Usuário atualizado");

        new UsuarioMapper().updateEntity(request, usuario);

        assertEquals("Usuário atualizado", usuario.getNome());
        assertEquals("senha-criptografada", usuario.getSenha());
        assertEquals(FuncaoUsuario.ADMIN, usuario.getFuncao());
        assertEquals("Usuário atualizado", new UsuarioMapper().toResponseDTO(usuario).getNome());
    }

    @Test
    void materialMapperConverteCamposDeObservacaoEIdsRelacionados() {
        MaterialRequestDTO request = new MaterialRequestDTO();
        request.setTitulo("Título");
        request.setObservacao("Observação");
        request.setEditoraId(2L);
        request.setAutorId(3L);
        request.setAutorIds(Set.of(3L, 4L));
        request.setGeneroIds(Set.of(5L));

        MaterialMapper mapper = new MaterialMapper();
        Material material = mapper.toEntity(request);

        assertEquals("Observação", material.getObservacao());
        assertEquals(2L, material.getEditora().getId());
        assertEquals(3L, material.getAutor().getId());
        assertEquals(Set.of(3L, 4L),
                material.getAutores().stream().map(a -> a.getId()).collect(Collectors.toSet()));
        assertEquals(Set.of(5L),
                material.getGeneros().stream().map(g -> g.getId()).collect(Collectors.toSet()));
        assertEquals("Observação", mapper.toResponseDTO(material).getObservacao());
    }
}
