package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BloqueioUsuarioRequestDTO {
    @NotNull(message = "O status de bloqueio é obrigatório")
    private Boolean bloqueado;
}
