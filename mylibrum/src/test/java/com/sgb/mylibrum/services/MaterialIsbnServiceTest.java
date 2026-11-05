package com.sgb.mylibrum.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;

import com.sgb.mylibrum.dtos.mapper.MaterialMapper;
import com.sgb.mylibrum.dtos.request.CadastroIsbnRequestDTO;
import com.sgb.mylibrum.dtos.response.CadastroIsbnResponseDTO;
import com.sgb.mylibrum.dtos.response.ConsultaIsbnResponseDTO;
import com.sgb.mylibrum.dtos.response.MaterialResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.integrations.isbn.BuscaIsbnService;
import com.sgb.mylibrum.integrations.isbn.LivroExterno;
import com.sgb.mylibrum.repositories.AutorRepository;
import com.sgb.mylibrum.repositories.EditoraRepository;
import com.sgb.mylibrum.repositories.GeneroRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.security.AutorizacaoService;

class MaterialIsbnServiceTest {

    private static final String ISBN = "9780132350884";

    private BuscaIsbnService busca;
    private MaterialRepository materialRepository;
    private AutorRepository autorRepository;
    private EditoraRepository editoraRepository;
    private GeneroRepository generoRepository;
    private MaterialMapper mapper;
    private MaterialIsbnService service;

    @BeforeEach
    void setUp() {
        busca = mock(BuscaIsbnService.class);
        materialRepository = mock(MaterialRepository.class);
        autorRepository = mock(AutorRepository.class);
        editoraRepository = mock(EditoraRepository.class);
        generoRepository = mock(GeneroRepository.class);
        mapper = mock(MaterialMapper.class);
        AutorizacaoService autorizacao = mock(AutorizacaoService.class);
        when(autorizacao.filialLogadaId()).thenReturn(1L);
        when(materialRepository.findByIsbn(anyString())).thenReturn(Optional.empty());
        when(autorRepository.findFirstByNomeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(editoraRepository.findFirstByNomeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(generoRepository.findFirstByNomeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(autorRepository.save(any(Autor.class))).thenAnswer(i -> i.getArgument(0));
        when(editoraRepository.save(any(Editora.class))).thenAnswer(i -> i.getArgument(0));
        when(generoRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(materialRepository.saveAndFlush(any(Material.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toResponseDTO(any(Material.class))).thenAnswer(i -> {
            MaterialResponseDTO dto = new MaterialResponseDTO();
            dto.setTitulo(((Material) i.getArgument(0)).getTitulo());
            return dto;
        });
        service = new MaterialIsbnService(busca, materialRepository, autorRepository, editoraRepository,
                generoRepository, mapper, autorizacao, mock(PlatformTransactionManager.class));
    }

    private static LivroExterno livro() {
        return new LivroExterno(ISBN, "Clean Code", null, List.of("Robert Martin", "robert martin"), "Prentice Hall",
                2008, 464, null, List.of("Tecnologia"), null, null, "GOOGLE_BOOKS");
    }

    @Test
    void isbnJaCadastradoNaoChamaFontesExternas() {
        when(materialRepository.findByIsbn(ISBN)).thenReturn(Optional.of(new Material()));

        CadastroIsbnResponseDTO r = service.cadastrar(new CadastroIsbnRequestDTO("978-0-13-235088-4", null, null,
                null, null, null, null, null, null));
        ConsultaIsbnResponseDTO c = service.consultar(ISBN);

        assertFalse(r.criado());
        assertTrue(c.jaCadastrado());
        verify(busca, never()).buscar(anyString(), any());
    }

    @Test
    void consultaNaoGravaEIndicaOQueSeriaCriado() {
        when(busca.buscar(ISBN, 1L)).thenReturn(Optional.of(livro()));

        ConsultaIsbnResponseDTO c = service.consultar(ISBN);

        assertTrue(c.encontradoExternamente());
        assertTrue(c.criariaEditora());
        assertEquals(List.of("Robert Martin"), c.novosAutores());
        assertEquals(List.of("Tecnologia"), c.novosGeneros());
        verify(materialRepository, never()).saveAndFlush(any());
    }

    @Test
    void cadastraReaproveitandoPorNomeECriandoOQueFalta() {
        when(busca.buscar(ISBN, 1L)).thenReturn(Optional.of(livro()));
        Editora existente = new Editora();
        existente.setNome("PRENTICE HALL");
        when(editoraRepository.findFirstByNomeIgnoreCase("Prentice Hall")).thenReturn(Optional.of(existente));

        CadastroIsbnResponseDTO r = service.cadastrar(new CadastroIsbnRequestDTO(ISBN, null, null, null, null, null,
                null, null, null));

        assertTrue(r.criado());
        assertEquals("GOOGLE_BOOKS", r.fonte());
        verify(editoraRepository, never()).save(any());
        verify(autorRepository).save(any(Autor.class)); // duplicado por maiúsculas vira um só
    }

    @Test
    void semNenhumaFonteCadastraOMinimo() {
        when(busca.buscar(ISBN, 1L)).thenReturn(Optional.empty());

        CadastroIsbnResponseDTO r = service.cadastrar(new CadastroIsbnRequestDTO(ISBN, null, null, null, null, null,
                null, null, null));

        assertTrue(r.criado());
        assertEquals("MINIMO", r.fonte());
        assertEquals("ISBN " + ISBN + " (completar cadastro)", r.material().getTitulo());
    }

    @Test
    void ajustesDoRequestSubstituemDadosExternos() {
        when(busca.buscar(ISBN, 1L)).thenReturn(Optional.of(livro()));

        CadastroIsbnResponseDTO r = service.cadastrar(new CadastroIsbnRequestDTO(ISBN, "Outro Título", null, null,
                null, null, null, null, null));

        assertEquals("Outro Título", r.material().getTitulo());
    }

    @Test
    void corridaDeIsbnDuplicadoDevolveOExistente() {
        when(busca.buscar(ISBN, 1L)).thenReturn(Optional.empty());
        when(materialRepository.saveAndFlush(any(Material.class))).thenThrow(new DataIntegrityViolationException("x"));
        when(materialRepository.findByIsbn(ISBN)).thenReturn(Optional.empty(), Optional.of(new Material()));

        CadastroIsbnResponseDTO r = service.cadastrar(new CadastroIsbnRequestDTO(ISBN, null, null, null, null, null,
                null, null, null));

        assertFalse(r.criado());
    }
}
