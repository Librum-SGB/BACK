package com.sgb.mylibrum.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

/** {@code criado} = false quando o ISBN já existia; {@code fonte} = origem dos dados ou "MINIMO" se nada foi achado. */
@Schema(description = "Resultado do cadastro por ISBN. Indica se houve criação, a fonte dos dados e o material resultante.")
public record CadastroIsbnResponseDTO(boolean criado, String fonte, MaterialResponseDTO material) {
}
