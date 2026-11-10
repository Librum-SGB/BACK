package com.sgb.mylibrum.dtos.request;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;

@Data
@Schema(description = "Dados cadastrais de usuário. Nome, e-mail e filial são obrigatórios; senha é obrigatória apenas na criação. Campos administrativos são limitados pelo perfil autenticado.")
public class UsuarioRequestDTO {
    public interface Criacao {}

    @NotBlank(message = "O nome é obrigatório")
    private String nome;
    
    private String cpf;
    
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String email;

    @NotBlank(message = "A senha é obrigatória", groups = Criacao.class)
    private String senha;
    
    private String telefone;
    
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
    private FuncaoUsuario funcao;
    private String matriculaFuncionario;
    private OffsetDateTime ultimoAcesso;
}