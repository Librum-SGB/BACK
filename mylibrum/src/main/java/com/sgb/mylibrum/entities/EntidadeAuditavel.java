package com.sgb.mylibrum.entities;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class EntidadeAuditavel {

    @Column(name = "data_criacao", updatable = false)
    private OffsetDateTime dataCriacao;

    @Column(name = "data_ultima_atualizacao")
    private OffsetDateTime dataUltimaAtualizacao;

    @Column(columnDefinition = "boolean default false")
    private Boolean excluido = false;

    @Column(columnDefinition = "boolean default true")
    private Boolean ativo = true;

    @PrePersist
    protected void aoCriar() {
        OffsetDateTime agora = OffsetDateTime.now(ZoneOffset.UTC);
        if (dataCriacao == null) {
            dataCriacao = agora;
        }
        dataUltimaAtualizacao = agora;
    }

    @PreUpdate
    protected void aoAtualizar() {
        dataUltimaAtualizacao = OffsetDateTime.now(ZoneOffset.UTC);
    }
}