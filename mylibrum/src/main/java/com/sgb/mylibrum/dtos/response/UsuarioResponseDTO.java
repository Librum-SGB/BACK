package com.sgb.mylibrum.dtos.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private Long filialId;
    private String endereco;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private Integer limiteLivros;
    private Boolean estaBloqueado;
    private OffsetDateTime dataCriacao;
    private OffsetDateTime dataUltimaAtualizacao;
}