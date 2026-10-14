package com.sgb.mylibrum.dtos.mapper;

import com.sgb.mylibrum.dtos.request.HistoricoMultaRequestDTO;
import com.sgb.mylibrum.dtos.response.HistoricoMultaResponseDTO;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.HistoricoMulta;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class HistoricoMultaMapper {

    public HistoricoMulta toEntity(HistoricoMultaRequestDTO dto) {
        HistoricoMulta multa = new HistoricoMulta();
        updateEntity(dto, multa);
        return multa;
    }

    public void updateEntity(HistoricoMultaRequestDTO dto, HistoricoMulta multa) {
        BeanUtils.copyProperties(dto, multa,
                "id", "dataCriacao", "dataUltimaAtualizacao", "status", "dataGeracao",
                "dataPagamento", "emprestimoId", "usuarioId");
        if (dto.getEmprestimoId() != null) {
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setId(dto.getEmprestimoId());
            multa.setEmprestimo(emprestimo);
        }
    }

    public HistoricoMultaResponseDTO toResponseDTO(HistoricoMulta multa) {
        HistoricoMultaResponseDTO dto = new HistoricoMultaResponseDTO();
        BeanUtils.copyProperties(multa, dto);
        if (multa.getEmprestimo() != null) {
            dto.setEmprestimoId(multa.getEmprestimo().getId());
        }
        if (multa.getUsuario() != null) {
            dto.setUsuarioId(multa.getUsuario().getId());
        }
        return dto;
    }
}
