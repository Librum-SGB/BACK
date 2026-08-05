package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.HistoricoMultaRequestDTO;
import com.sgb.mylibrum.dtos.response.HistoricoMultaResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;
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
    public List<HistoricoMultaResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HistoricoMultaResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Histórico de multa não encontrado")));
    }

    @Transactional
    public HistoricoMultaResponseDTO create(HistoricoMultaRequestDTO dto) {
        HistoricoMulta entity = new HistoricoMulta();
        BeanUtils.copyProperties(dto, entity);
        if (dto.getEmprestimoId() != null) {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setId(dto.getEmprestimoId());
            entity.setEmprestimo(emprestimo);
        }
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public HistoricoMultaResponseDTO update(Long id, HistoricoMultaRequestDTO dto) {
        HistoricoMulta entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Histórico de multa não encontrado"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao");
        if (dto.getEmprestimoId() != null) {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setId(dto.getEmprestimoId());
            entity.setEmprestimo(emprestimo);
        }
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private HistoricoMultaResponseDTO toResponseDTO(HistoricoMulta entity) {
        HistoricoMultaResponseDTO dto = new HistoricoMultaResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getEmprestimo() != null) dto.setEmprestimoId(entity.getEmprestimo().getId());
        return dto;
    }
}