package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.FilialRequestDTO;
import com.sgb.mylibrum.dtos.mapper.FilialMapper;
import com.sgb.mylibrum.dtos.response.FilialResponseDTO;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.repositories.FilialRepository;
import com.sgb.mylibrum.repositories.specifications.AtivoSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class FilialService {

    private final FilialRepository repository;
    private final FilialMapper mapper;

    @Transactional(readOnly = true)
    public List<FilialResponseDTO> findAll(Boolean ativo) {
        log.debug("Buscando filiais: ativo={}", ativo);
        return repository.findAll(AtivoSpecification.ativo(ativo)).stream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public FilialResponseDTO findById(Long id) {
        log.debug("Buscando filial: id={}", id);
        return mapper.toResponseDTO(repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Filial nao encontrada: id={}", id);
                    return new RuntimeException("Filial n?o encontrada");
                }));
    }

    @Transactional
    public FilialResponseDTO create(FilialRequestDTO dto) {
        Filial filial = repository.save(mapper.toEntity(dto));
        log.info("Filial criada: id={}", filial.getId());
        return mapper.toResponseDTO(filial);
    }

    @Transactional
    public FilialResponseDTO update(Long id, FilialRequestDTO dto) {
        Filial entity = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Filial nao encontrada para atualizacao: id={}", id);
                    return new RuntimeException("Filial n?o encontrada");
                });
        mapper.updateEntity(dto, entity);
        Filial atualizada = repository.save(entity);
        log.info("Filial atualizada: id={}", atualizada.getId());
        return mapper.toResponseDTO(atualizada);
    }

    @Transactional
    public void delete(Long id) {
        Filial filial = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Filial nao encontrada para exclusao: id={}", id);
                    return new RuntimeException("Filial n?o encontrada");
                });
        filial.setExcluido(true);
        repository.save(filial);
        log.info("Filial excluida: id={}", filial.getId());
    }
}