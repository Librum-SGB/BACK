package com.sgb.mylibrum.integrations.isbn;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.sgb.mylibrum.repositories.ConfiguracaoRepository;
import com.sgb.mylibrum.services.ConfiguracaoChaves;

import lombok.extern.slf4j.Slf4j;

/** Consulta as fontes na ordem; ao falhar ou não encontrar, segue para a próxima. */
@Service
@Slf4j
public class BuscaIsbnService {

    private static final Duration VALIDADE_CACHE = Duration.ofMinutes(10);

    private record EntradaCache(LivroExterno livro, long criadoEm) {
    }

    private final List<FonteIsbn> fontes;
    private final ConfiguracaoRepository configuracaoRepository;
    private final Map<String, EntradaCache> cache = new ConcurrentHashMap<>();

    public BuscaIsbnService(List<FonteIsbn> fontes, ConfiguracaoRepository configuracaoRepository) {
        this.fontes = fontes;
        this.configuracaoRepository = configuracaoRepository;
    }

    public Optional<LivroExterno> buscar(String isbn13, Long filialId) {
        EntradaCache em = cache.get(isbn13);
        if (em != null) {
            if (System.currentTimeMillis() - em.criadoEm() < VALIDADE_CACHE.toMillis()) {
                return Optional.of(em.livro());
            }
            cache.remove(isbn13);
        }

        ChavesIsbn chaves = lerChaves(filialId);
        for (FonteIsbn fonte : fontes) {
            if (!fonte.disponivel(chaves)) {
                log.debug("Fonte {} ignorada (sem chave configurada)", fonte.nome());
                continue;
            }
            try {
                Optional<LivroExterno> livro = fonte.buscar(isbn13, chaves);
                if (livro.isPresent() && livro.get().utilizavel()) {
                    cache.put(isbn13, new EntradaCache(livro.get(), System.currentTimeMillis()));
                    return livro;
                }
                log.info("Fonte {} não encontrou o ISBN {}", fonte.nome(), isbn13);
            } catch (RuntimeException e) {
                // só o tipo da falha é registrado, para não vazar chaves presentes em URLs/mensagens
                log.warn("Fonte {} falhou para o ISBN {}: {}", fonte.nome(), isbn13, e.getClass().getSimpleName());
            }
        }
        return Optional.empty();
    }

    private ChavesIsbn lerChaves(Long filialId) {
        if (filialId == null) {
            return ChavesIsbn.VAZIAS;
        }
        return new ChavesIsbn(valor(filialId, ConfiguracaoChaves.ISBN_GOOGLE_APIKEY),
                valor(filialId, ConfiguracaoChaves.ISBN_ISBNDB_APIKEY),
                valor(filialId, ConfiguracaoChaves.ISBN_WORLDCAT_APIKEY),
                valor(filialId, ConfiguracaoChaves.ISBN_WORLDCAT_SECRET));
    }

    private String valor(Long filialId, String chave) {
        return configuracaoRepository.findByFilialIdAndChave(filialId, chave)
                .map(c -> c.getValor()).filter(ChavesIsbn::presente).map(String::trim).orElse(null);
    }
}
