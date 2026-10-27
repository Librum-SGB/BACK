package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EstanteRequestDTO;
import com.sgb.mylibrum.dtos.mapper.EstanteMapper;
import com.sgb.mylibrum.dtos.response.EstanteResponseDTO;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.EstantePrateleira;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.exceptions.ResourceNotFoundException;
import com.sgb.mylibrum.repositories.EstanteRepository;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.FilialRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Comparator;
@Service
@RequiredArgsConstructor
public class EstanteService {

    private final EstanteRepository repository;
    private final FilialRepository filialRepository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstanteMapper mapper;

    @Transactional(readOnly = true)
    public List<EstanteResponseDTO> findAll(Boolean ativo) {
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<EstanteResponseDTO> findByFilialId(Long filialId, Boolean ativo) {
        if (!filialRepository.existsById(filialId)) {
            throw new ResourceNotFoundException("Filial não encontrada");
        }
        return repository.findAll(AtivoSpecification.<Estante>ativo(ativo)
                        .and(AtivoSpecification.igual("filial.id", filialId))).stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EstanteResponseDTO findById(Long id) {
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada")));
    }

    @Transactional
    public EstanteResponseDTO create(EstanteRequestDTO dto) {
        Estante entity = mapper.toEntity(dto);
        entity.setFilial(filialDoUsuarioAutenticado());
        aplicarPrateleiras(dto, entity, List.of());
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public EstanteResponseDTO update(Long id, EstanteRequestDTO dto) {
        Estante entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada"));
        Long filialUsuarioId = filialDoUsuarioAutenticado().getId();
        if (!filialUsuarioId.equals(entity.getFilial().getId())) {
            throw new ResourceNotFoundException("Estante não encontrada");
        }
        List<EstantePrateleira> anteriores = new ArrayList<>(entity.getPrateleiras());
        mapper.updateEntity(dto, entity);
        aplicarPrateleiras(dto, entity, anteriores);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Estante estante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada"));
        if (exemplarRepository.existsIncludingDeletedByEstanteId(id)) {
            throw new IllegalStateException("Limpe a estante antes de excluí-la");
        }
        estante.setExcluido(true);
        repository.save(estante);
    }

    @Transactional
    public void clear(Long id) {
        Estante estante = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estante não encontrada"));
        if (!filialDoUsuarioAutenticado().getId().equals(estante.getFilial().getId())) {
            throw new ResourceNotFoundException("Estante não encontrada");
        }
        exemplarRepository.detachAllFromEstante(id);
    }

    private void aplicarPrateleiras(EstanteRequestDTO dto, Estante estante,
                                    List<EstantePrateleira> anteriores) {
        List<com.sgb.mylibrum.dtos.request.EstantePrateleiraRequestDTO> configuracao = dto.getPrateleiras();
        if (configuracao == null) {
            int quantidade = dto.getQtdPrateleiras() == null ? 0 : Math.max(0, dto.getQtdPrateleiras());
            int posicaoMaxima = dto.getCapacidade() == null || dto.getCapacidade() <= 0
                    ? 100 : dto.getCapacidade();
            configuracao = new ArrayList<>();
            for (int i = 1; i <= quantidade; i++) {
                var prateleira = new com.sgb.mylibrum.dtos.request.EstantePrateleiraRequestDTO();
                prateleira.setNome("Prateleira " + i);
                prateleira.setPosicaoInicial(1);
                prateleira.setPosicaoFinal(posicaoMaxima);
                prateleira.setOrdem(i);
                if (i <= anteriores.size()) {
                    prateleira.setId(anteriores.get(i - 1).getId());
                }
                configuracao.add(prateleira);
            }
        } else {
            estante.setQtdPrateleiras(configuracao.size());
        }

        Set<String> nomes = new HashSet<>();
        Set<Integer> ordens = new HashSet<>();
        Set<Long> idsRecebidos = new HashSet<>();
        Set<EstantePrateleira> mantidas = Collections.newSetFromMap(new IdentityHashMap<>());
        Map<Long, EstantePrateleira> anterioresPorId = new HashMap<>();
        Map<Integer, EstantePrateleira> anterioresPorOrdem = new HashMap<>();
        for (EstantePrateleira anterior : anteriores) {
            anterioresPorId.put(anterior.getId(), anterior);
            anterioresPorOrdem.put(anterior.getOrdem(), anterior);
        }

        List<EstantePrateleira> atualizadas = new ArrayList<>();
        List<String[]> renomes = new ArrayList<>();
        for (int i = 0; i < configuracao.size(); i++) {
            var item = configuracao.get(i);
            if (item.getNome() == null || item.getNome().isBlank()
                    || item.getPosicaoInicial() == null || item.getPosicaoFinal() == null
                    || item.getPosicaoInicial() <= 0 || item.getPosicaoFinal() < item.getPosicaoInicial()
                    || !nomes.add(item.getNome().trim())) {
                throw new IllegalArgumentException("Configuração de prateleira inválida");
            }
            int ordem = item.getOrdem() == null ? i + 1 : item.getOrdem();
            if (ordem <= 0 || !ordens.add(ordem)) {
                throw new IllegalArgumentException("A ordem das prateleiras deve ser única e positiva");
            }

            EstantePrateleira prateleira;
            if (item.getId() != null) {
                if (!idsRecebidos.add(item.getId())) {
                    throw new IllegalArgumentException("A mesma prateleira foi enviada mais de uma vez");
                }
                prateleira = anterioresPorId.get(item.getId());
                if (prateleira == null) {
                    throw new IllegalArgumentException("A prateleira não pertence a esta estante");
                }
            } else {
                prateleira = anterioresPorOrdem.get(ordem);
                if (prateleira == null || mantidas.contains(prateleira)) {
                    prateleira = new EstantePrateleira();
                    prateleira.setEstante(estante);
                } else {
                    idsRecebidos.add(prateleira.getId());
                }
            }

            if (prateleira.getNome() != null
                    && !prateleira.getNome().equals(item.getNome().trim())) {
                renomes.add(new String[]{prateleira.getNome(), item.getNome().trim()});
            }
            prateleira.setNome(item.getNome().trim());
            prateleira.setPosicaoInicial(item.getPosicaoInicial());
            prateleira.setPosicaoFinal(item.getPosicaoFinal());
            prateleira.setOrdem(ordem);
            mantidas.add(prateleira);
            atualizadas.add(prateleira);
        }

        for (EstantePrateleira anterior : anteriores) {
            if (!mantidas.contains(anterior)
                    && exemplarRepository.countByEstanteIdAndPrateleiraIncludingDeleted(
                    estante.getId(), anterior.getNome()) > 0) {
                throw new IllegalStateException("Não é possível remover uma prateleira que possui exemplares");
            }
        }

        List<String[]> renomesTemporarios = new ArrayList<>();
        for (String[] renome : renomes) {
            String temporario = "tmp-" + UUID.randomUUID();
            exemplarRepository.renamePrateleira(estante.getId(), renome[0], temporario);
            renomesTemporarios.add(new String[]{temporario, renome[1]});
        }
        for (String[] renome : renomesTemporarios) {
            exemplarRepository.renamePrateleira(estante.getId(), renome[0], renome[1]);
        }

        estante.getPrateleiras().removeIf(prateleira -> !mantidas.contains(prateleira));
        for (EstantePrateleira prateleira : atualizadas) {
            if (estante.getPrateleiras().stream().noneMatch(existente -> existente == prateleira)) {
                estante.getPrateleiras().add(prateleira);
            }
        }
        estante.getPrateleiras().sort(Comparator.comparing(EstantePrateleira::getOrdem));
        if (dto.getPrateleiras() == null) {
            estante.setQtdPrateleiras(atualizadas.size());
        }
    }

    private Filial filialDoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("É necessário estar autenticado para cadastrar uma estante");
        }

        var usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
        if (usuario.getFilial() == null || usuario.getFilial().getId() == null) {
            throw new IllegalStateException("O usuário autenticado não possui uma filial vinculada");
        }

        return filialRepository.findById(usuario.getFilial().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Filial do usuário não encontrada"));
    }
}