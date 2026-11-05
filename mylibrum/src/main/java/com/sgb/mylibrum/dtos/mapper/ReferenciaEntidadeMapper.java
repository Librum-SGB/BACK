package com.sgb.mylibrum.dtos.mapper;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.entities.Emprestimo;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.entities.Usuario;

/** Converte ids em referências de entidade (apenas com o id preenchido) e vice-versa. */
@Component
public class ReferenciaEntidadeMapper {

    public Filial toFilial(Long id) {
        return referencia(id, Filial::new, (e) -> e.setId(id));
    }

    public Usuario toUsuario(Long id) {
        return referencia(id, Usuario::new, (e) -> e.setId(id));
    }

    public Exemplar toExemplar(Long id) {
        return referencia(id, Exemplar::new, (e) -> e.setId(id));
    }

    public Material toMaterial(Long id) {
        return referencia(id, Material::new, (e) -> e.setId(id));
    }

    public Estante toEstante(Long id) {
        return referencia(id, Estante::new, (e) -> e.setId(id));
    }

    public Emprestimo toEmprestimo(Long id) {
        return referencia(id, Emprestimo::new, (e) -> e.setId(id));
    }

    public Autor toAutor(Long id) {
        return referencia(id, Autor::new, (e) -> e.setId(id));
    }

    public Editora toEditora(Long id) {
        return referencia(id, Editora::new, (e) -> e.setId(id));
    }

    public Genero toGenero(Long id) {
        return referencia(id, Genero::new, (e) -> e.setId(id));
    }

    public Long toId(Autor autor) {
        return autor == null ? null : autor.getId();
    }

    public Long toId(Genero genero) {
        return genero == null ? null : genero.getId();
    }

    private <T> T referencia(Long id, Supplier<T> criador, Consumer<T> definirId) {
        if (id == null) {
            return null;
        }
        T entidade = criador.get();
        definirId.accept(entidade);
        return entidade;
    }
}
