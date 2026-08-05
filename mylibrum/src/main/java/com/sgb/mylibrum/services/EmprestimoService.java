package com.sgb.mylibrum.services;

import com.sgb.mylibrum.dtos.request.EmprestimoRequestDTO;
import com.sgb.mylibrum.dtos.response.EmprestimoResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Gestor;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.repositories.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository repository;

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO findById(Long id) {
        return toResponseDTO(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado")));
    }

    @Transactional
    public EmprestimoResponseDTO create(EmprestimoRequestDTO dto) {
        Emprestimo entity = new Emprestimo();
        BeanUtils.copyProperties(dto, entity);
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public EmprestimoResponseDTO update(Long id, EmprestimoRequestDTO dto) {
        Emprestimo entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
        BeanUtils.copyProperties(dto, entity, "id", "dataCriacao", "dataUltimaAtualizacao", "dataSaida");
        setRelacionamentos(dto, entity);
        return toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    private void setRelacionamentos(EmprestimoRequestDTO dto, Emprestimo entity) {
        if (dto.getUsuarioId() != null) {
            Usuario u = new Usuario(); u.setId(dto.getUsuarioId()); entity.setUsuario(u);
        }
        if (dto.getExemplarId() != null) {
            Exemplar e = new Exemplar(); e.setId(dto.getExemplarId()); entity.setExemplar(e);
        }
        if (dto.getGestorId() != null) {
            Gestor g = new Gestor(); g.setId(dto.getGestorId()); entity.setGestor(g);
        }
    }

    private EmprestimoResponseDTO toResponseDTO(Emprestimo entity) {
        EmprestimoResponseDTO dto = new EmprestimoResponseDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getUsuario() != null) dto.setUsuarioId(entity.getUsuario().getId());
        if (entity.getExemplar() != null) dto.setExemplarId(entity.getExemplar().getId());
        if (entity.getGestor() != null) dto.setGestorId(entity.getGestor().getId());
        return dto;
    }
}