package com.sgb.mylibrum.entities;

import com.sgb.mylibrum.entities.enums.StatusMulta;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@org.hibernate.annotations.SQLRestriction("excluido = false")
@Table(name = "historico_multas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class HistoricoMulta extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprestimo_id", nullable = false)
    private Emprestimo emprestimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "dias_atraso", nullable = false)
    private Integer diasAtraso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusMulta status = StatusMulta.PENDENTE;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(name = "data_geracao", nullable = false)
    private OffsetDateTime dataGeracao = OffsetDateTime.now();
}