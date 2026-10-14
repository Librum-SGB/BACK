package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.EmprestimoRequestDTO;
import com.sgb.mylibrum.dtos.response.EmprestimoResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Usuario;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class EmprestimoMapper {

    public Emprestimo toEntity(EmprestimoRequestDTO dto) {
        Emprestimo emprestimo = new Emprestimo();
        BeanUtils.copyProperties(dto, emprestimo,
                "id", "dataSaida", "dataDevolucaoEfetivada", "renovacoesContagem",
                "usuarioId", "exemplarId", "usuarioResponsavelId");
        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            emprestimo.setUsuario(usuario);
        }
        if (dto.getExemplarId() != null) {
            Exemplar exemplar = new Exemplar();
            exemplar.setId(dto.getExemplarId());
            emprestimo.setExemplar(exemplar);
        }
        if (dto.getUsuarioResponsavelId() != null) {
            Usuario responsavel = new Usuario();
            responsavel.setId(dto.getUsuarioResponsavelId());
            emprestimo.setUsuarioResponsavel(responsavel);
        }
        return emprestimo;
    }

    public EmprestimoResponseDTO toResponseDTO(Emprestimo emprestimo) {
        EmprestimoResponseDTO dto = new EmprestimoResponseDTO();
        BeanUtils.copyProperties(emprestimo, dto);
        if (emprestimo.getUsuario() != null) {
            dto.setUsuarioId(emprestimo.getUsuario().getId());
        }
        if (emprestimo.getExemplar() != null) {
            dto.setExemplarId(emprestimo.getExemplar().getId());
        }
        if (emprestimo.getUsuarioResponsavel() != null) {
            dto.setUsuarioResponsavelId(emprestimo.getUsuarioResponsavel().getId());
        }
        return dto;
    }
}
