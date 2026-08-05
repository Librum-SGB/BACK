package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioRequestDTO {
    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    
    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;
    
    @NotBlank(message = "O e-mail é obrigatório")
    private String email;
    
    @NotBlank(message = "O telefone é obrigatório")
    private String telefone;
    
    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate dataNascimento;
    
    @NotNull(message = "A filial é obrigatória")
    private Long filialId;
    
    private String endereco;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private Integer limiteLivros;
    private Boolean estaBloqueado;
}