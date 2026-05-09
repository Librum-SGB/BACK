package com.sgb.mylibrum.entities;

import com.sgb.mylibrum.entities.enums.StatusExemplar;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "exemplares")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Exemplar extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filial_id", nullable = false)
    private Filial filial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estante_id", nullable = false)
    private Estante estante;

    @Column(length = 50)
    private String prateleira;

    @Column(length = 50)
    private String posicao;

    @Column(name = "codigo_barras", unique = true, nullable = false, length = 100)
    private String codigoBarras;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar default 'DISPONIVEL'")
    private StatusExemplar status = StatusExemplar.DISPONIVEL;

    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao = LocalDate.now();

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}