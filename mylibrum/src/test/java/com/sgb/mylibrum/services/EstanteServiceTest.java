package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.mapper.EstanteMapper;
import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EstanteRepository;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.FilialRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class EstanteServiceTest {

    @Mock
    private EstanteRepository estanteRepository;
    @Mock
    private FilialRepository filialRepository;
    @Mock
    private ExemplarRepository exemplarRepository;
    @Mock
    private UsuarioRepository usuarioRepository;

    private EstanteService service;

    @BeforeEach
    void setUp() {
        service = new EstanteService(
                estanteRepository,
                filialRepository,
                exemplarRepository,
                usuarioRepository,
                new EstanteMapper());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "bibliotecaria@exemplo.com", "token", List.of()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void criaEstanteUsandoFilialDoUsuarioAutenticado() {
        Filial filial = new Filial();
        filial.setId(42L);
        Usuario usuario = new Usuario();
        usuario.setFilial(filial);

        when(usuarioRepository.findByEmail("bibliotecaria@exemplo.com")).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(42L)).thenReturn(Optional.of(filial));
        when(estanteRepository.save(any(Estante.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EstanteRequestDTO request = new EstanteRequestDTO();
        request.setNome("Estante principal");
        request.setLocalizacao("Corredor A");
        request.setQtdPrateleiras(3);
        request.setCapacidade(25);

        var response = service.create(request);

        assertEquals(42L, response.getFilialId());
        assertEquals(3, response.getPrateleiras().size());
        assertEquals("Prateleira 1", response.getPrateleiras().get(0).getNome());
        assertEquals(1, response.getPrateleiras().get(0).getPosicaoInicial());
        assertEquals(25, response.getPrateleiras().get(0).getPosicaoFinal());
        verify(usuarioRepository).findByEmail("bibliotecaria@exemplo.com");
        verify(filialRepository).findById(42L);
    }

    @Test
    void persisteConfiguracaoPersonalizadaDePrateleiras() {
        Filial filial = new Filial();
        filial.setId(42L);
        Usuario usuario = new Usuario();
        usuario.setFilial(filial);
        when(usuarioRepository.findByEmail("bibliotecaria@exemplo.com")).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(42L)).thenReturn(Optional.of(filial));
        when(estanteRepository.save(any(Estante.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EstanteRequestDTO request = new EstanteRequestDTO();
        request.setNome("Estante infantil");
        request.setLocalizacao("Corredor B");
        var config = new com.sgb.mylibrum.dtos.request.EstantePrateleiraRequestDTO();
        config.setNome("Contos");
        config.setPosicaoInicial(4);
        config.setPosicaoFinal(18);
        config.setOrdem(2);
        request.setPrateleiras(List.of(config));

        var response = service.create(request);

        assertEquals(1, response.getQtdPrateleiras());
        assertEquals("Contos", response.getPrateleiras().get(0).getNome());
        assertEquals(4, response.getPrateleiras().get(0).getPosicaoInicial());
        assertEquals(18, response.getPrateleiras().get(0).getPosicaoFinal());
    }

    @Test
    void limparDesvinculaExemplaresEExcluirEstanteExigeEstanteVazia() {
        Estante estante = new Estante();
        estante.setId(17L);
        Filial filial = new Filial();
        filial.setId(42L);
        estante.setFilial(filial);
        Usuario usuario = new Usuario();
        usuario.setFilial(filial);
        when(estanteRepository.findById(17L)).thenReturn(Optional.of(estante));
        when(usuarioRepository.findByEmail("bibliotecaria@exemplo.com")).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(42L)).thenReturn(Optional.of(filial));
        when(exemplarRepository.existsIncludingDeletedByEstanteId(17L)).thenReturn(false);

        service.clear(17L);
        service.delete(17L);

        verify(exemplarRepository).detachAllFromEstante(17L);
        verify(estanteRepository).save(estante);
    }

    @Test
    void naoExcluiEstanteComExemplaresAindaVinculados() {
        Estante estante = new Estante();
        estante.setId(17L);
        when(estanteRepository.findById(17L)).thenReturn(Optional.of(estante));
        when(exemplarRepository.existsIncludingDeletedByEstanteId(17L)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.delete(17L));

        verify(estanteRepository, never()).save(estante);
    }

    @Test
    void renomearConfiguracaoAtualizaNomeDaPrateleiraNosExemplares() {
        Filial filial = new Filial();
        filial.setId(42L);
        Estante estante = new Estante();
        estante.setId(9L);
        estante.setFilial(filial);
        var anterior = new com.sgb.mylibrum.entities.EstantePrateleira();
        anterior.setId(12L);
        anterior.setEstante(estante);
        anterior.setNome("Prateleira antiga");
        anterior.setPosicaoInicial(1);
        anterior.setPosicaoFinal(10);
        anterior.setOrdem(1);
        estante.setPrateleiras(new java.util.ArrayList<>(List.of(anterior)));
        Usuario usuario = new Usuario();
        usuario.setFilial(filial);
        when(estanteRepository.findById(9L)).thenReturn(Optional.of(estante));
        when(usuarioRepository.findByEmail("bibliotecaria@exemplo.com")).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(42L)).thenReturn(Optional.of(filial));
        when(estanteRepository.save(any(Estante.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EstanteRequestDTO request = new EstanteRequestDTO();
        request.setNome("Estante");
        request.setLocalizacao("Corredor");
        var nova = new com.sgb.mylibrum.dtos.request.EstantePrateleiraRequestDTO();
        nova.setId(12L);
        nova.setNome("Ficção");
        nova.setPosicaoInicial(1);
        nova.setPosicaoFinal(10);
        request.setPrateleiras(List.of(nova));

        var response = service.update(9L, request);

        assertEquals(1, estante.getPrateleiras().size());
        assertTrue(estante.getPrateleiras().get(0) == anterior);
        assertEquals("Ficção", anterior.getNome());
        assertEquals(1, response.getPrateleiras().size());
        assertEquals(12L, response.getPrateleiras().get(0).getId());
        ArgumentCaptor<String> oldNames = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> newNames = ArgumentCaptor.forClass(String.class);
        verify(exemplarRepository, times(2))
                .renamePrateleira(eq(9L), oldNames.capture(), newNames.capture());
        assertEquals("Prateleira antiga", oldNames.getAllValues().get(0));
        assertTrue(newNames.getAllValues().get(0).startsWith("tmp-"));
        assertEquals(newNames.getAllValues().get(0), oldNames.getAllValues().get(1));
        assertEquals("Ficção", newNames.getAllValues().get(1));
    }

    @Test
    void rejeitaCriacaoQuandoUsuarioNaoPossuiFilial() {
        when(usuarioRepository.findByEmail("bibliotecaria@exemplo.com"))
                .thenReturn(Optional.of(new Usuario()));

        EstanteRequestDTO request = new EstanteRequestDTO();
        request.setNome("Estante principal");
        request.setLocalizacao("Corredor A");

        assertThrows(IllegalStateException.class, () -> service.create(request));
    }

    @Test
    void rejeitaCriacaoQuandoUsuarioAutenticadoNaoExiste() {
        when(usuarioRepository.findByEmail("bibliotecaria@exemplo.com"))
                .thenReturn(Optional.empty());

        EstanteRequestDTO request = new EstanteRequestDTO();
        request.setNome("Estante principal");
        request.setLocalizacao("Corredor A");

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }
}
