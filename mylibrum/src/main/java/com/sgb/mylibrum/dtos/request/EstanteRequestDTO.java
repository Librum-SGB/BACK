package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "Dados de uma estante física vinculada a uma filial. Filial, nome e localização são obrigatórios.")
public class  EstanteRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A localização é obrigatória")
    private String localizacao;
    
    private Integer capacidade;
    private Integer qtdPrateleiras;
    private List<@Valid EstantePrateleiraRequestDTO> prateleiras;
}