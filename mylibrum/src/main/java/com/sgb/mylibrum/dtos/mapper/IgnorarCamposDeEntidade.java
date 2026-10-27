package com.sgb.mylibrum.dtos.mapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mapstruct.Mapping;

/** Campos de identidade e auditoria que nunca vêm do DTO de entrada. */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
@Mapping(target = "id", ignore = true)
@Mapping(target = "dataCriacao", ignore = true)
@Mapping(target = "dataUltimaAtualizacao", ignore = true)
@Mapping(target = "excluido", ignore = true)
@Mapping(target = "ativo", ignore = true)
public @interface IgnorarCamposDeEntidade {
}
