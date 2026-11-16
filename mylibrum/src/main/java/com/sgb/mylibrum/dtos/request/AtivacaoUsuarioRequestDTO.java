package com.sgb.mylibrum.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Novo estado de ativação da conta de usuário. Contas inativas não entram no sistema.")
public class AtivacaoUsuarioRequestDTO {
    @NotNull(message = "O status de ativação é obrigatório")
    private Boolean ativo;
}
