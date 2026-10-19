package com.sgb.mylibrum.entities;

import com.sgb.mylibrum.entities.enums.PrioridadeTarefa;
import com.sgb.mylibrum.entities.enums.StatusTarefa;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@org.hibernate.annotations.SQLRestriction("excluido = false")
@Table(name = "lista_tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ListaTarefa extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar default 'MEDIA'")
    private PrioridadeTarefa prioridade = PrioridadeTarefa.MEDIA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar default 'PENDENTE'")
    private StatusTarefa status = StatusTarefa.PENDENTE;

    private LocalDate prazo;
}