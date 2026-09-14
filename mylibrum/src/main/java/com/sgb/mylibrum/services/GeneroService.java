package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.GeneroRequestDTO;
import com.sgb.mylibrum.dtos.response.GeneroResponseDTO;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.repositories.GeneroRepository;
import com.sgb.mylibrum.dtos.mapper.GeneroMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneroService {

    private final GeneroRepository repository;
    private final GeneroMapper mapper;

    @Transactional(readOnly = true)
    public List<GeneroResponseDTO> findAll() {
        log.debug("Buscando todos os generos ativos");
        return repository.findAllByAtivoTrueAndExcluidoFalse().stream().map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public GeneroResponseDTO findById(Long id) {
        log.info("Buscando genero com id={}", id);
        Optional<Genero> genero = repository.findByIdAndAtivoTrueAndExcluidoFalse(id);
        if (genero.isPresent()) {
            log.info("Genero com id={}", genero.get().getId());
        } else {
            log.warn("Genero não encontrado com id ={} ", id);
        }
        return genero.map(mapper::toResponseDTO).orElse(null);
    }

    @Transactional
    public GeneroResponseDTO create(GeneroRequestDTO dto) {
        log.info("Criando novo genero: nome={}", dto.getNome());
        Genero entity = mapper.toEntity(dto);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public GeneroResponseDTO update(Long id, GeneroRequestDTO dto) {
        Genero entity = repository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        log.info("Genero id={} atualizando Genero: nome={}", id, entity.getNome());
        mapper.updateEntityFromDTO(dto, entity);
        log.info("Genero id={} atualizado com sucesso: novoNome={}", id, entity.getNome());
        repository.saveAndFlush(entity);
        return mapper.toResponseDTO(entity);
    }

    @Transactional
    public boolean delete(Long id) {
        Genero genero = repository.findById(id).orElse(null);
        if (genero != null) {
            desativar(genero);
            genero.setExcluido(true);
            log.info("Genero id={} - nome= {} excluido com sucesso", genero.getId(), genero.getNome());
            return true;
        }
        log.warn("Tentativa de excluir genero inexistente: id={}", id);
        return false;
    }

    @Transactional
    public boolean desativar(Long id) {
        Genero genero = repository.findById(id).orElse(null);
        if (genero != null) {
            genero.setAtivo(false);
            log.info("Genero id={} - nome= {} desativado com sucesso", genero.getId(), genero.getNome());
            return true;
        }
        log.warn("Tentativa de desativar genero inexistente: id={}", id);
        return false;
    }

    @Transactional
    public boolean ativar(Long id) {
        Genero genero = repository.findById(id).orElse(null);
        if (genero != null) {
            if (!genero.getAtivo()) {
                genero.setAtivo(true);
                log.info("Genero id={} - nome= {} ativando com sucesso", genero.getId(), genero.getNome());
                return true;
            }
            log.warn("Tentativa de ativar genero já ativo: id={}", id);
            return true;
        }
        log.warn("Tentativa de ativando genero inexistente: id={}", id);
        return false;
    }

    private void desativar(Genero genero) {
        if (genero != null) {
            if (genero.getAtivo()) {
                genero.setAtivo(false);
                log.info("Genero id={} - nome= {} desativado com sucesso", genero.getId(), genero.getNome());
            } else {
                log.warn("Tentativa de desativar genero já desativo");
            }
            return;
        } else {
            log.warn("Tentativa de desativar genero inexistente");
        }
    }
}