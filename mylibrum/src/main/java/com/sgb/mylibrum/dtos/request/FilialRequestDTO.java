package com.sgb.mylibrum.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FilialRequestDTO {
    @NotBlank(message = "O nome fantasia é obrigatório")
    private String nomeFantasia;
    
    @NotBlank(message = "A razão social é obrigatória")
    private String razaoSocial;
    
    @NotBlank(message = "O CNPJ é obrigatório")
    private String cnpj;
    
    private String inscricaoEstadual;
    
    @NotBlank(message = "O endereço é obrigatório")
    private String endereco;
    
    private String numero;
    private String complemento;
    
    @NotBlank(message = "O bairro é obrigatório")
    private String bairro;
    
    @NotBlank(message = "A cidade é obrigatória")
    private String cidade;
    
    @NotBlank(message = "O estado é obrigatório")
    private String estado;
    
    @NotBlank(message = "O CEP é obrigatório")
    private String cep;
    
    private String telefone;
    private String email;
    private Boolean ativo;
}