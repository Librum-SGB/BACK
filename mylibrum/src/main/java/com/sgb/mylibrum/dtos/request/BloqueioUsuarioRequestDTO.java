package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Novo estado de bloqueio da conta de usuário.")
public class BloqueioUsuarioRequestDTO {
    @NotNull(message = "O status de bloqueio é obrigatório")
    private Boolean bloqueado;
}
