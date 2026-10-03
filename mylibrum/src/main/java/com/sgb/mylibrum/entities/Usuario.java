package com.sgb.mylibrum.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.sgb.mylibrum.entities.enums.Funcao;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Usuario extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(unique = true, length = 11)
    private String cpf;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 255)
    private String senha;

    @Column(unique = true, length = 15)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filial_id", nullable = false)
    private Filial filial;

    @Column(length = 150)
    private String endereco;

    @Column(length = 10)
    private String numero;

    @Column(length = 100)
    private String bairro;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    @Column(length = 8)
    private String cep;

    @Column(name = "limite_livros", columnDefinition = "integer default 3")
    private Integer limiteLivros = 3;

    @Column(name = "esta_bloqueado", columnDefinition = "boolean default false")
    private Boolean estaBloqueado = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Funcao funcao = Funcao.USUARIO;

    @Column(name = "matricula_funcionario", unique = true, length = 20)
    private String matriculaFuncionario;

    @Column(name = "ultimo_acesso")
    private OffsetDateTime ultimoAcesso;
}