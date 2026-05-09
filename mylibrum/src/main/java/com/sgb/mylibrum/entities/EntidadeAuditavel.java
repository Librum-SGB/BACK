package com.sgb.mylibrum.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class EntidadeAuditavel {

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private OffsetDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_ultima_atualizacao")
    private OffsetDateTime dataUltimaAtualizacao;
}