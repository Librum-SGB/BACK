package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.mapper.ListaTarefaMapper;
import com.sgb.mylibrum.entities.ListaTarefa;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import com.sgb.mylibrum.repositories.ListaTarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListaTarefaServiceTest {

    @Mock
    private ListaTarefaRepository repository;

    private ListaTarefaService service;

    @BeforeEach
    void setUp() {
        service = new ListaTarefaService(repository, new ListaTarefaMapper());
    }

    @Test
    void deveListarTarefasAssociadasAoGestor() {
        Usuario gestor = new Usuario();
        gestor.setId(12L);
        ListaTarefa tarefa = new ListaTarefa();
        tarefa.setId(3L);
        tarefa.setUsuario(gestor);
        tarefa.setDescricao("Organizar acervo");
        tarefa.setStatus(StatusTarefa.PENDENTE);
        when(repository.findByUsuarioId(12L)).thenReturn(List.of(tarefa));

        var resultado = service.findByGestorId(12L);

        assertEquals(1, resultado.size());
        assertEquals(12L, resultado.get(0).getUsuarioId());
        assertEquals("Organizar acervo", resultado.get(0).getDescricao());
        verify(repository).findByUsuarioId(12L);
    }
}
