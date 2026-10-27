package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.ConfiguracaoRequestDTO;
import com.sgb.mylibrum.dtos.request.MaterialRequestDTO;
import com.sgb.mylibrum.dtos.request.UsuarioRequestDTO;
import com.sgb.mylibrum.entities.Configuracao;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.EstantePrateleira;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DtoMapperTest {

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

    @Test
    void estanteMapperExpõeConfiguracaoDePrateleiras() {
        Estante estante = new Estante();
        EstantePrateleira prateleira = new EstantePrateleira();
        prateleira.setNome("Referência");
        prateleira.setPosicaoInicial(2);
        prateleira.setPosicaoFinal(12);
        prateleira.setOrdem(1);
        estante.setPrateleiras(java.util.List.of(prateleira));

        var response = new EstanteMapper().toResponseDTO(estante);

        assertEquals("Referência", response.getPrateleiras().get(0).getNome());
        assertEquals(2, response.getPrateleiras().get(0).getPosicaoInicial());
        assertEquals(12, response.getPrateleiras().get(0).getPosicaoFinal());
    }
}
