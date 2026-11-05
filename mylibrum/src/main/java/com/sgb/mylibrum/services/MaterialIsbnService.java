package com.sgb.mylibrum.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.sgb.mylibrum.dtos.mapper.MaterialMapper;
import com.sgb.mylibrum.dtos.request.CadastroIsbnRequestDTO;
import com.sgb.mylibrum.dtos.response.CadastroIsbnResponseDTO;
import com.sgb.mylibrum.dtos.response.ConsultaIsbnResponseDTO;
import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.integrations.isbn.BuscaIsbnService;
import com.sgb.mylibrum.integrations.isbn.IsbnUtil;
import com.sgb.mylibrum.integrations.isbn.LivroExterno;
import com.sgb.mylibrum.repositories.AutorRepository;
import com.sgb.mylibrum.repositories.EditoraRepository;
import com.sgb.mylibrum.repositories.GeneroRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.security.AutorizacaoService;

@Slf4j
@Service
public class MaterialIsbnService {

    static final String FONTE_MINIMO = "MINIMO";
    static final String EDITORA_DESCONHECIDA = "Desconhecida";

    private final BuscaIsbnService buscaIsbnService;
    private final MaterialRepository materialRepository;
    private final AutorRepository autorRepository;
    private final EditoraRepository editoraRepository;
    private final GeneroRepository generoRepository;
    private final MaterialMapper materialMapper;
    private final AutorizacaoService autorizacaoService;
    private final TransactionTemplate tx;

    public MaterialIsbnService(BuscaIsbnService buscaIsbnService, MaterialRepository materialRepository,
            AutorRepository autorRepository, EditoraRepository editoraRepository,
            GeneroRepository generoRepository, MaterialMapper materialMapper,
            AutorizacaoService autorizacaoService, PlatformTransactionManager transactionManager) {
        this.buscaIsbnService = buscaIsbnService;
        this.materialRepository = materialRepository;
        this.autorRepository = autorRepository;
        this.editoraRepository = editoraRepository;
        this.generoRepository = generoRepository;
        this.materialMapper = materialMapper;
        this.autorizacaoService = autorizacaoService;
        this.tx = new TransactionTemplate(transactionManager);
    }

    /** Prévia: não grava nada. */
    public ConsultaIsbnResponseDTO consultar(String isbnInformado) {
        log.debug("Consultando ISBN={}", IsbnUtil.normalizar(isbnInformado));
        String isbn = IsbnUtil.normalizar(isbnInformado);

        ConsultaIsbnResponseDTO existente = tx.execute(s -> materialRepository.findByIsbn(isbn)
                .map(m -> new ConsultaIsbnResponseDTO(isbn, true, materialMapper.toResponseDTO(m), false, null,
                        false, List.of(), List.of()))
                .orElse(null));
        if (existente != null) {
            log.debug("Material ja cadastrado localmente para ISBN={}", isbn);
            return existente;
        }

        Optional<LivroExterno> encontrado = buscaIsbnService.buscar(isbn, autorizacaoService.filialLogadaId());
        if (encontrado.isEmpty()) {
            log.warn("ISBN nao encontrado em fontes externas: isbn={}", isbn);
            return new ConsultaIsbnResponseDTO(isbn, false, null, false, null, false, List.of(), List.of());
        }
        LivroExterno livro = encontrado.get();
        log.debug("ISBN encontrado em fonte externa: isbn={}, fonte={}", isbn, livro.fonte());
        return tx.execute(s -> new ConsultaIsbnResponseDTO(isbn, false, null, true, livro,
                livro.editora() != null && editoraRepository.findFirstByNomeIgnoreCase(livro.editora()).isEmpty(),
                limparNomes(livro.autores()).stream()
                        .filter(n -> autorRepository.findFirstByNomeIgnoreCase(n).isEmpty()).toList(),
                limparNomes(livro.generos()).stream()
                        .filter(n -> generoRepository.findFirstByNomeIgnoreCase(n).isEmpty()).toList()));
    }

    public CadastroIsbnResponseDTO cadastrar(CadastroIsbnRequestDTO req) {
        log.info("Iniciando cadastro por ISBN: isbn={}", IsbnUtil.normalizar(req.isbn()));
        String isbn = IsbnUtil.normalizar(req.isbn());

        CadastroIsbnResponseDTO existente = buscarExistente(isbn);
        if (existente != null) {
            log.debug("Cadastro ignorado, material ja existe: isbn={}", isbn);
            return existente;
        }

        Optional<LivroExterno> externo = buscaIsbnService.buscar(isbn, autorizacaoService.filialLogadaId());
        String fonte = externo.map(LivroExterno::fonte).orElse(FONTE_MINIMO);
        log.debug("Fonte selecionada para cadastro ISBN={}: fonte={}", isbn, fonte);
        LivroExterno livro = aplicarAjustes(isbn, externo.orElse(null), req);

        try {
            CadastroIsbnResponseDTO resposta = tx.execute(s -> new CadastroIsbnResponseDTO(true, fonte,
                    materialMapper.toResponseDTO(materialRepository.saveAndFlush(montar(livro)))));
            log.info("Material cadastrado por ISBN: isbn={}, fonte={}", isbn, fonte);
            return resposta;
        } catch (DataIntegrityViolationException e) {
            log.error("Falha de integridade no cadastro de material por ISBN={}", isbn, e);
            // outro cadastro do mesmo ISBN venceu a corrida
            CadastroIsbnResponseDTO agora = buscarExistente(isbn);
            if (agora != null) {
                return agora;
            }
            throw e;
        }
    }

    private CadastroIsbnResponseDTO buscarExistente(String isbn) {
        return tx.execute(s -> materialRepository.findByIsbn(isbn)
                .map(m -> new CadastroIsbnResponseDTO(false, "LOCAL", materialMapper.toResponseDTO(m)))
                .orElse(null));
    }

    private LivroExterno aplicarAjustes(String isbn, LivroExterno base, CadastroIsbnRequestDTO req) {
        String titulo = primeiroTexto(req.titulo(), base == null ? null : base.titulo());
        if (titulo == null) {
            titulo = "ISBN " + isbn + " (completar cadastro)";
        }
        return new LivroExterno(isbn, titulo,
                primeiroTexto(req.subtitulo(), base == null ? null : base.subtitulo()),
                req.autores() != null ? req.autores() : (base == null ? null : base.autores()),
                primeiroTexto(req.editora(), base == null ? null : base.editora()),
                req.anoPublicacao() != null ? req.anoPublicacao() : (base == null ? null : base.anoPublicacao()),
                req.quantidadePaginas() != null ? req.quantidadePaginas()
                        : (base == null ? null : base.quantidadePaginas()),
                primeiroTexto(req.sinopse(), base == null ? null : base.sinopse()),
                req.generos() != null ? req.generos() : (base == null ? null : base.generos()),
                base == null ? null : base.idioma(), base == null ? null : base.capaUrl(),
                base == null ? FONTE_MINIMO : base.fonte());
    }

    private Material montar(LivroExterno livro) {
        Material m = new Material();
        m.setIsbn(livro.isbn());
        m.setTitulo(truncar(livro.titulo(), 255));
        m.setSubtitulo(truncar(livro.subtitulo(), 255));
        m.setSinopse(livro.sinopse());
        m.setAnoPublicacao(livro.anoPublicacao());
        m.setQuantidadePaginas(livro.quantidadePaginas());

        String nomeEditora = livro.editora() == null || livro.editora().isBlank() ? EDITORA_DESCONHECIDA
                : livro.editora().trim();
        m.setEditora(resolverEditora(truncar(nomeEditora, 150)));

        List<Autor> autores = new ArrayList<>();
        for (String nome : limparNomes(livro.autores())) {
            autores.add(resolverAutor(truncar(nome, 150)));
        }
        m.setAutores(new HashSet<>(autores));
        if (!autores.isEmpty()) {
            m.setAutor(autores.get(0));
        }

        Set<Genero> generos = new HashSet<>();
        for (String nome : limparNomes(livro.generos())) {
            generos.add(resolverGenero(truncar(nome, 100)));
        }
        m.setGeneros(generos);
        return m;
    }

    private Editora resolverEditora(String nome) {
        return editoraRepository.findFirstByNomeIgnoreCase(nome).orElseGet(() -> {
            Editora e = new Editora();
            e.setNome(nome);
            log.info("Editora criada durante cadastro ISBN: nome={}", nome);
            return editoraRepository.save(e);
        });
    }

    private Autor resolverAutor(String nome) {
        return autorRepository.findFirstByNomeIgnoreCase(nome).orElseGet(() -> {
            Autor a = new Autor();
            a.setNome(nome);
            log.info("Autor criado durante cadastro ISBN: nome={}", nome);
            return autorRepository.save(a);
        });
    }

    private Genero resolverGenero(String nome) {
        return generoRepository.findFirstByNomeIgnoreCase(nome).orElseGet(() -> {
            Genero g = new Genero();
            g.setNome(nome);
            log.info("Genero criado durante cadastro ISBN: nome={}", nome);
            return generoRepository.save(g);
        });
    }

    /** Remove vazios e duplicados (sem diferenciar maiúsculas), preservando a ordem. */
    private static List<String> limparNomes(List<String> nomes) {
        Map<String, String> unicos = new LinkedHashMap<>();
        if (nomes != null) {
            for (String n : nomes) {
                if (n != null && !n.isBlank()) {
                    unicos.putIfAbsent(n.trim().toLowerCase(), n.trim());
                }
            }
        }
        return new ArrayList<>(unicos.values());
    }

    private static String primeiroTexto(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a.trim();
        }
        return b == null || b.isBlank() ? null : b.trim();
    }

    private static String truncar(String s, int max) {
        return s == null || s.length() <= max ? s : s.substring(0, max);
    }
}
