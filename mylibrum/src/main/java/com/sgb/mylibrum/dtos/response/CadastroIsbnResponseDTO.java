package com.sgb.mylibrum.dtos.response;

/** {@code criado} = false quando o ISBN já existia; {@code fonte} = origem dos dados ou "MINIMO" se nada foi achado. */
public record CadastroIsbnResponseDTO(boolean criado, String fonte, MaterialResponseDTO material) {
}
