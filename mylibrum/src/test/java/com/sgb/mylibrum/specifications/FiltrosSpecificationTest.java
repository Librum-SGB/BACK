package com.sgb.mylibrum.specifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.server.ResponseStatusException;

import com.sgb.mylibrum.entities.Autor;
import com.sgb.mylibrum.entities.Editora;
import com.sgb.mylibrum.entities.Estante;
import com.sgb.mylibrum.entities.Exemplar;
import com.sgb.mylibrum.entities.Filial;
import com.sgb.mylibrum.entities.Genero;
import com.sgb.mylibrum.entities.Material;
import com.sgb.mylibrum.entities.Usuario;
import com.sgb.mylibrum.entities.enums.FuncaoUsuario;
import com.sgb.mylibrum.entities.enums.StatusExemplar;
import com.sgb.mylibrum.entities.enums.TipoMaterial;
import com.sgb.mylibrum.repositories.ExemplarRepository;
import com.sgb.mylibrum.repositories.MaterialRepository;
import com.sgb.mylibrum.repositories.UsuarioRepository;

import jakarta.persistence.EntityManager;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.docker.compose.enabled=false"
})
class FiltrosSpecificationTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private MaterialRepository materiais;
    @Autowired
    private ExemplarRepository exemplares;
    @Autowired
    private UsuarioRepository usuarios;

    private Editora editoraA;
    private Editora editoraB;
    private Autor autor1;
    private Autor autor2;
    private Genero ficcao;
    private Genero tecnico;
    private Material dom;
    private Material java;
    private Material revista;
    private Filial filial1;
    private Filial filial2;
    private Estante estante1;

    @BeforeEach
    void preparar() {
        editoraA = persistir(editora("Editora A"));
        editoraB = persistir(editora("Editora B"));
        autor1 = persistir(autor("Machado"));
        autor2 = persistir(autor("Bloch"));
        ficcao = persistir(genero("Ficção"));
        tecnico = persistir(genero("Técnico"));

        dom = persistir(material("Dom Casmurro", "1111111111111", TipoMaterial.LIVRO, 1899, "Romance", editoraA, autor1));
        dom.getGeneros().add(ficcao);
        java = persistir(material("Java Efetivo", "2222222222222", TipoMaterial.LIVRO, 2018, "Programação", editoraB, autor2));
        java.getAutores().add(autor1);
        java.getAutores().add(autor2);
        java.getGeneros().add(tecnico);
        java.getGeneros().add(ficcao);
        revista = persistir(material("Revista Tech", null, TipoMaterial.PERIODICO, 2022, "Tecnologia", editoraB, autor2));
        revista.setAtivo(false);

        filial1 = persistir(filial("Central", "11111111111111"));
        filial2 = persistir(filial("Norte", "22222222222222"));
        estante1 = new Estante();
        estante1.setFilial(filial1);
        estante1.setNome("E1");
        estante1.setLocalizacao("Andar 1");
        persistir(estante1);
        em.flush();
        em.clear();
    }

    @Test
    void materialCombinaFiltrosComAnd() {
        var r = materiais.findAll(MaterialSpecification.filtrar(filtroMaterial(
                "casmurro", null, null, editoraA.getId(), null, null, null, null, null, true)));
        assertEquals(List.of("Dom Casmurro"), titulos(r));

        r = materiais.findAll(MaterialSpecification.filtrar(filtroMaterial(
                "casmurro", null, null, editoraB.getId(), null, null, null, null, null, true)));
        assertTrue(r.isEmpty());
    }

    @Test
    void materialAutorPrincipalOuNN() {
        var r = materiais.findAll(MaterialSpecification.filtrar(filtroMaterial(
                null, null, null, null, autor1.getId(), null, null, null, null, true)));
        assertEquals(List.of("Dom Casmurro", "Java Efetivo"), titulos(r));
    }

    @Test
    void materialGeneroNaoDuplicaResultados() {
        var r = materiais.findAll(MaterialSpecification.filtrar(filtroMaterial(
                null, null, null, null, null, ficcao.getId(), null, null, null, true)));
        assertEquals(List.of("Dom Casmurro", "Java Efetivo"), titulos(r));

        r = materiais.findAll(MaterialSpecification.filtrar(filtroMaterial(
                null, null, null, null, autor2.getId(), null, null, null, null, true)));
        assertEquals(List.of("Java Efetivo"), titulos(r));
    }

    @Test
    void materialAnoTipoTemaEAtivo() {
        assertEquals(List.of("Java Efetivo"), titulos(materiais.findAll(MaterialSpecification.filtrar(
                filtroMaterial(null, null, null, null, null, null, 2000, 2020, null, true)))));
        assertEquals(List.of("Revista Tech"), titulos(materiais.findAll(MaterialSpecification.filtrar(
                filtroMaterial(null, null, TipoMaterial.PERIODICO, null, null, null, null, null, "tecno", false)))));
        assertEquals(3, materiais.findAll(MaterialSpecification.filtrar(
                filtroMaterial(null, null, null, null, null, null, null, null, null, null))).size());
        assertEquals(List.of("Dom Casmurro"), titulos(materiais.findAll(MaterialSpecification.filtrar(
                filtroMaterial(null, "1111", null, null, null, null, null, 1900, null, true)))));
    }

    @Test
    void materialIntervaloInvalidoGera400() {
        var ex = assertThrows(ResponseStatusException.class, () -> MaterialSpecification.filtrar(filtroMaterial(
                null, null, null, null, null, null, 2020, 2000, null, true)));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void exemplarFiltraPorIdsStatusEData() {
        persistir(exemplar(dom, filial1, "COD-1", StatusExemplar.DISPONIVEL, LocalDate.of(2024, 1, 10)));
        persistir(exemplar(java, filial1, "COD-2", StatusExemplar.EMPRESTADO, LocalDate.of(2025, 6, 1)));
        persistir(exemplar(java, filial2, "COD-3", StatusExemplar.DISPONIVEL, LocalDate.of(2025, 7, 1)));
        em.flush();
        em.clear();

        assertEquals(2, exemplares.findAll(ExemplarSpecification.filtrar(new ExemplarFiltro(
                null, java.getId(), null, null, null, null, null, true))).size());
        assertEquals(1, exemplares.findAll(ExemplarSpecification.filtrar(new ExemplarFiltro(
                StatusExemplar.DISPONIVEL, java.getId(), filial2.getId(), null, null, null, null, true))).size());
        assertEquals(2, exemplares.findAll(ExemplarSpecification.filtrar(new ExemplarFiltro(
                null, null, null, null, null, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), true))).size());
        assertEquals(1, exemplares.findAll(ExemplarSpecification.filtrar(new ExemplarFiltro(
                null, null, null, null, "cod-1", null, null, null))).size());
        assertEquals(3, exemplares.findAll(ExemplarSpecification.filtrar(new ExemplarFiltro(
                null, null, null, null, null, null, null, true))).size());
    }

    @Test
    void usuarioFiltraPorCamposCombinados() {
        persistir(usuario("Ana Silva", "ana@x.com", "12345678901", FuncaoUsuario.ADMIN, filial1, "Recife", false));
        persistir(usuario("Bruno Lima", "bruno@x.com", "10987654321", FuncaoUsuario.USUARIO, filial2, "Olinda", true));
        em.flush();
        em.clear();

        assertEquals(1, usuarios.findAll(UsuarioSpecification.filtrar(new UsuarioFiltro(
                null, null, "ANA@", null, null, null, null, null, true))).size());
        assertEquals(1, usuarios.findAll(UsuarioSpecification.filtrar(new UsuarioFiltro(
                null, null, null, null, null, filial2.getId(), true, "linda", true))).size());
        assertEquals(1, usuarios.findAll(UsuarioSpecification.filtrar(new UsuarioFiltro(
                null, null, null, "12345678901", FuncaoUsuario.ADMIN, null, null, null, true))).size());
        assertEquals(0, usuarios.findAll(UsuarioSpecification.filtrar(new UsuarioFiltro(
                null, null, null, null, FuncaoUsuario.ADMIN, filial2.getId(), null, null, true))).size());
    }

    @Test
    void ativoPadraoESomenteAtivos() {
        assertEquals(Boolean.TRUE, FiltrosUtil.interpretarAtivo(null));
        assertEquals(Boolean.TRUE, FiltrosUtil.interpretarAtivo(""));
        assertEquals(Boolean.FALSE, FiltrosUtil.interpretarAtivo("false"));
        assertNull(FiltrosUtil.interpretarAtivo("todos"));
        assertThrows(ResponseStatusException.class, () -> FiltrosUtil.interpretarAtivo("talvez"));
    }

    private MaterialFiltro filtroMaterial(String titulo, String isbn, TipoMaterial tipo, Long editoraId,
            Long autorId, Long generoId, Integer de, Integer ate, String tema, Boolean ativo) {
        return new MaterialFiltro(titulo, isbn, tipo, editoraId, autorId, generoId, de, ate, tema, ativo);
    }

    private List<String> titulos(List<Material> lista) {
        return lista.stream().map(Material::getTitulo).sorted().toList();
    }

    private <T> T persistir(T entidade) {
        em.persist(entidade);
        return entidade;
    }

    private Editora editora(String nome) {
        Editora e = new Editora();
        e.setNome(nome);
        return e;
    }

    private Autor autor(String nome) {
        Autor a = new Autor();
        a.setNome(nome);
        return a;
    }

    private Genero genero(String nome) {
        Genero g = new Genero();
        g.setNome(nome);
        return g;
    }

    private Material material(String titulo, String isbn, TipoMaterial tipo, int ano, String tema,
            Editora editora, Autor autor) {
        Material m = new Material();
        m.setTitulo(titulo);
        m.setIsbn(isbn);
        m.setTipo(tipo);
        m.setAnoPublicacao(ano);
        m.setTema(tema);
        m.setEditora(editora);
        m.setAutor(autor);
        return m;
    }

    private Filial filial(String nome, String cnpj) {
        Filial f = new Filial();
        f.setNomeFantasia(nome);
        f.setRazaoSocial(nome + " LTDA");
        f.setCnpj(cnpj);
        f.setEndereco("Rua 1");
        f.setBairro("Centro");
        f.setCidade("Recife");
        f.setEstado("PE");
        f.setCep("50000000");
        return f;
    }

    private Exemplar exemplar(Material material, Filial filial, String codigo, StatusExemplar status,
            LocalDate aquisicao) {
        Exemplar e = new Exemplar();
        e.setMaterial(material);
        e.setFilial(filial);
        e.setEstante(estante1);
        e.setCodigoBarras(codigo);
        e.setStatus(status);
        e.setDataAquisicao(aquisicao);
        return e;
    }

    private Usuario usuario(String nome, String email, String cpf, FuncaoUsuario funcao, Filial filial,
            String cidade, boolean bloqueado) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setCpf(cpf);
        u.setFuncao(funcao);
        u.setFilial(filial);
        u.setCidade(cidade);
        u.setEstaBloqueado(bloqueado);
        return u;
    }
}
