package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import lombok.Data;
import java.util.List;

@Data
public class EstanteRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A localização é obrigatória")
    private String localizacao;
    
    private Integer capacidade;
    private Integer qtdPrateleiras;
    private List<@Valid EstantePrateleiraRequestDTO> prateleiras;
}