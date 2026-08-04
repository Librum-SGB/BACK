package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.HistoricoMultaDTO;
import com.sgb.mylibrum.entities.HistoricoMulta;
import com.sgb.mylibrum.repositories.HistoricoMultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricoMultaService {

    private final HistoricoMultaRepository repository;

    @Transactional(readOnly = true)
    public List<HistoricoMultaDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HistoricoMultaDTO findById(Long id) {
        HistoricoMulta entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Histórico de Multa não encontrado com id: " + id));
        return toDTO(entity);
    }

    @Transactional
    public HistoricoMultaDTO create(HistoricoMultaDTO dto) {
        HistoricoMulta entity = new HistoricoMulta();
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public HistoricoMultaDTO update(Long id, HistoricoMultaDTO dto) {
        HistoricoMulta entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Histórico de Multa não encontrado com id: " + id));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private HistoricoMultaDTO toDTO(HistoricoMulta entity) {
        HistoricoMultaDTO dto = new HistoricoMultaDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getEmprestimo() != null) dto.setEmprestimoId(entity.getEmprestimo().getId());
        return dto;
    }
}