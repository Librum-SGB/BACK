-- Dados iniciais de desenvolvimento. O Flyway registra esta migration em flyway_schema_history.

INSERT INTO filiais (id, nome_fantasia, razao_social, endereco, cidade, bairro, estado, cep, cnpj, email, numero, telefone, inscricao_estadual, complemento, ativo, excluido)
VALUES (1, 'Biblioteca Central', 'Biblioteca Municipal Central Ltda', 'Av. Principal, 100', 'Cidade Exemplo', 'Centro', 'SP', '01000000', '12345678000100', 'contato@biblioteca.ex', '100', '11999990000', '123456', NULL, true, false),
       (2, 'Biblioteca Norte', 'Biblioteca Municipal Norte Ltda', 'Rua das Flores, 200', 'Cidade Exemplo', 'Jardim Norte', 'SP', '02000000', '22345678000101', 'norte@biblioteca.ex', '200', '11999990002', '223456', NULL, true, false),
       (3, 'Biblioteca Sul', 'Biblioteca Municipal Sul Ltda', 'Rua do Lago, 300', 'Cidade Exemplo', 'Vila Sul', 'SP', '03000000', '32345678000102', 'sul@biblioteca.ex', '300', '11999990003', '323456', NULL, true, false),
       (4, 'Biblioteca Leste', 'Biblioteca Municipal Leste Ltda', 'Av. Leste, 400', 'Cidade Exemplo', 'Vila Leste', 'SP', '04000000', '42345678000103', 'leste@biblioteca.ex', '400', '11999990004', '423456', NULL, true, false),
       (5, 'Biblioteca Oeste', 'Biblioteca Municipal Oeste Ltda', 'Rua Oeste, 500', 'Cidade Exemplo', 'Vila Oeste', 'SP', '05000000', '52345678000104', 'oeste@biblioteca.ex', '500', '11999990005', '523456', NULL, true, false),
       (6, 'Biblioteca Central II', 'Biblioteca Municipal Central II Ltda', 'Av. Central, 600', 'Cidade Exemplo', 'Centro Novo', 'SP', '06000000', '62345678000105', 'central2@biblioteca.ex', '600', '11999990006', '623456', NULL, true, false)
ON CONFLICT DO NOTHING;

INSERT INTO editoras (id, nome, nacionalidade, ativo, excluido)
VALUES (1, 'Editora Exemplo', 'Brasil', true, false),
       (2, 'Editora Horizonte', 'Brasil', true, false),
       (3, 'Editora Aurora', 'Portugal', true, false),
       (4, 'Editora Caminhos', 'Brasil', true, false),
       (5, 'Editora Papiro', 'Argentina', true, false),
       (6, 'Editora Horizonte Sul', 'Brasil', true, false)
ON CONFLICT DO NOTHING;

INSERT INTO generos (id, nome, descricao, ativo, excluido)
VALUES (1, 'Ficcao', 'Obras de ficção geral', true, false),
    (2, 'Historia', 'Livros de história', true, false),
    (3, 'Ciencia', 'Divulgacao e pesquisa cientifica', true, false),
    (4, 'Fantasia', 'Narrativas de mundos imaginarios', true, false),
    (5, 'Biografia', 'Relatos sobre trajetorias de vida', true, false),
    (6, 'Poesia', 'Coletaneas e obras poeticas', true, false),
    (7, 'Tecnologia', 'Computacao e inovacao', true, false)
ON CONFLICT DO NOTHING;

INSERT INTO autores (id, nome, nacionalidade, biografia, data_nascimento, ativo, excluido)
VALUES (1, 'Joao Silva', 'Brasil', 'Autor exemplo.', '1970-05-12', true, false),
    (2, 'Maria Souza', 'Brasil', 'Outra autora exemplo.', '1980-08-20', true, false),
    (3, 'Clara Mendes', 'Brasil', 'Autora de narrativas contemporaneas.', '1978-03-14', true, false),
    (4, 'Pedro Nogueira', 'Portugal', 'Autor de ensaios e cronicas.', '1969-09-02', true, false),
    (5, 'Lia Campos', 'Brasil', 'Pesquisadora e autora de divulgacao.', '1985-01-27', true, false),
    (6, 'Rafael Costa', 'Brasil', 'Autor de obras sobre tecnologia.', '1982-06-18', true, false),
    (7, 'Sofia Martins', 'Argentina', 'Poeta e tradutora.', '1990-12-09', true, false)
ON CONFLICT DO NOTHING;

INSERT INTO usuarios (id, nome, cpf, telefone, email, senha, data_nascimento, filial_id, funcao, ativo, excluido)
VALUES (1, 'Usuário Geral', '12345678901', '11988887777', 'usuario@gmail.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', '1995-03-10', 1, 'USUARIO', true, false),
       (2, 'Ana Exemplo', '23456789012', '11977776666', 'ana@exemplo.com', '$2a$10$fr55WgsWbo46V8JXk.fonum3hisiPdi/qPJ9axO/6JSrVYvkm2y5i', '1998-07-22', 1, 'USUARIO', true, false),
    (3, 'Bruno Exemplo', '34567890123', '11966665555', 'bruno@exemplo.com', '$2a$10$NKjDfkoodmEUMjSpP1pI0OIDomNBt0iBzyiGq70gWYarySyfB0iva', '1992-11-05', 1, 'USUARIO', true, false),
    (4, 'Carla Exemplo', '45678901234', '11955554444', 'carla@exemplo.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', '1994-04-16', 2, 'USUARIO', true, false),
    (5, 'Diego Exemplo', '56789012345', '11944443333', 'diego@exemplo.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', '1988-10-03', 3, 'USUARIO', true, false),
    (6, 'Elisa Exemplo', '67890123456', '11933332222', 'elisa@exemplo.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', '2000-02-21', 4, 'USUARIO', true, false),
    (7, 'Fabio Exemplo', '78901234567', '11922221111', 'fabio@exemplo.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', '1991-08-12', 5, 'USUARIO', true, false),
    (8, 'Gabi Exemplo', '89012345678', '11911110000', 'gabi@exemplo.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', '1997-05-30', 6, 'USUARIO', true, false)
ON CONFLICT DO NOTHING;

INSERT INTO usuarios (id, nome, email, senha, matricula_funcionario, filial_id, ultimo_acesso, funcao, ativo, excluido)
VALUES (9, 'Admin', 'admin@gmail.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', 'MAT123', 1, now(), 'ADMIN', true, false),
       (10, 'Bibliotecária', 'bibliotecaria@gmail.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', 'MAT002', 2, now(), 'BIBLIOTECARIA', true, false),
       (15, 'Assistente Exemplo', 'assistente@gmail.com', '$2a$10$SqXtkYvOauuD6ULgkwVRselhWmdRknyIgFb171L/RF6Qzqrk701Si', 'MAT007', 1, now(), 'ASSISTENTE', true, false)
ON CONFLICT DO NOTHING;

INSERT INTO estantes (id, filial_id, nome, localizacao, capacidade, qtd_prateleiras, ativo, excluido)
VALUES (1, 1, 'Estante A1', 'A1', 100, 1, true, false),
    (2, 2, 'Estante B1', 'B1', 80, 1, true, false),
    (3, 2, 'Estante C1', 'C1', 90, 1, true, false),
    (4, 3, 'Estante D1', 'D1', 75, 1, true, false),
    (5, 4, 'Estante E1', 'E1', 110, 1, true, false),
    (6, 5, 'Estante F1', 'F1', 65, 1, true, false),
    (7, 6, 'Estante G1', 'G1', 120, 1, true, false)
ON CONFLICT DO NOTHING;

INSERT INTO materiais (id, titulo, tipo, subtitulo, sinopse, issn, tema, descricao, isbn, edicao, ano_publicacao, quantidade_paginas, editora_id, autor_id, ativo, excluido)
VALUES (1, 'Aprendendo Java', 'LIVRO', 'Uma introducao', 'Sinopse do livro.', NULL, 'Programacao', 'Descricao longa do livro.', '9781234567897', 1, 2020, 350, 1, 1, true, false),
    (2, 'Revista Exemplo', 'PERIODICO', NULL, 'Sinopse da revista.', '1234-5678', 'Ciencia', 'Descricao da revista.', '0001234560000', 1, 2021, 40, 1, 2, true, false),
    (3, 'Jardins de Papel', 'LIVRO', 'Contos breves', 'Uma coletanea de contos.', NULL, 'Literatura', 'Contos para leitores adultos.', '9781234567804', 1, 2022, 220, 2, 3, true, false),
    (4, 'Caminhos do Tempo', 'LIVRO', NULL, 'Ensaios sobre memoria.', NULL, 'Historia', 'Ensaios historicos introdutorios.', '9781234567811', 2, 2021, 310, 3, 4, true, false),
    (5, 'Ciencia no Cotidiano', 'LIVRO', 'Perguntas e respostas', 'Conceitos cientificos explicados.', NULL, 'Ciencia', 'Divulgacao cientifica para todos.', '9781234567828', 1, 2023, 280, 4, 5, true, false),
    (6, 'Codigo Aberto', 'LIVRO', NULL, 'Introducao a computacao.', NULL, 'Tecnologia', 'Conceitos de software e sistemas.', '9781234567835', 1, 2024, 360, 5, 6, true, false),
    (7, 'Versos da Cidade', 'LIVRO', 'Poesia reunida', 'Poemas sobre a vida urbana.', NULL, 'Poesia', 'Selecao de poemas contemporaneos.', '9781234567842', 1, 2020, 150, 6, 7, true, false)
ON CONFLICT DO NOTHING;

INSERT INTO materiais_autores (autor_id, material_id)
VALUES (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7)
ON CONFLICT DO NOTHING;

INSERT INTO materiais_generos (genero_id, material_id)
VALUES (1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7)
ON CONFLICT DO NOTHING;

INSERT INTO exemplares (id, material_id, filial_id, estante_id, prateleira, posicao, codigo_barras, status, data_aquisicao, observacoes, ativo, excluido)
VALUES (1, 1, 1, 1, 'P1', '01', 'CB-0001', 'DISPONIVEL', '2022-01-10', 'Exemplar em bom estado', true, false),
    (2, 1, 2, 2, 'P2', '05', 'CB-0002', 'DISPONIVEL', '2022-02-15', NULL, true, false),
    (3, 2, 2, 3, 'P1', '02', 'CB-0003', 'DISPONIVEL', '2023-01-12', NULL, true, false),
    (4, 3, 3, 4, 'P2', '03', 'CB-0004', 'DISPONIVEL', '2023-04-08', NULL, true, false),
    (5, 4, 4, 5, 'P1', '04', 'CB-0005', 'DISPONIVEL', '2024-02-19', NULL, true, false),
    (6, 5, 5, 6, 'P3', '01', 'CB-0006', 'DISPONIVEL', '2024-05-23', NULL, true, false),
    (7, 6, 6, 7, 'P2', '02', 'CB-0007', 'DISPONIVEL', '2025-03-15', NULL, true, false)
ON CONFLICT DO NOTHING;

INSERT INTO emprestimos (id, exemplar_id, usuario_responsavel_id, usuario_id, data_saida, data_devolucao_prevista, renovacoes_contagem, data_devolucao_efetivada, ativo, excluido)
VALUES (1, 1, 9, 1, now(), CURRENT_DATE + 20, 0, NULL, true, false),
       (2, 3, 10, 4, now(), CURRENT_DATE + 14, 0, NULL, true, false),
       (3, 4, 10, 5, now(), CURRENT_DATE + 21, 1, NULL, true, false),
       (4, 5, 10, 6, now(), CURRENT_DATE + 10, 0, NULL, true, false),
       (5, 6, 10, 7, now(), CURRENT_DATE + 18, 0, NULL, true, false),
       (6, 7, 10, 8, now(), CURRENT_DATE + 30, 0, NULL, true, false)
ON CONFLICT DO NOTHING;

INSERT INTO historico_multas (id, emprestimo_id, dias_atraso, valor, pago, data_pagamento, ativo, excluido)
VALUES (1, 1, 0, 0.00, false, NULL, true, false),
       (2, 2, 2, 4.50, false, NULL, true, false),
       (3, 3, 5, 12.00, true, CURRENT_DATE - 2, true, false),
       (4, 4, 1, 2.00, false, NULL, true, false),
       (5, 5, 7, 18.75, true, CURRENT_DATE - 1, true, false),
       (6, 6, 3, 8.25, false, NULL, true, false)
ON CONFLICT DO NOTHING;

INSERT INTO lista_tarefas (id, usuario_id, descricao, prioridade, concluida, ativo, excluido)
VALUES (1, 9, 'Reorganizar estantes da seção A', 'MEDIA', false, true, false),
       (2, 10, 'Conferir devolucoes da semana', 'ALTA', false, true, false),
       (3, 10, 'Atualizar catalogo de novidades', 'MEDIA', false, true, false),
       (4, 10, 'Revisar materiais em manutencao', 'URGENTE', false, true, false),
       (5, 10, 'Preparar atividades de leitura', 'BAIXA', false, true, false),
       (6, 10, 'Organizar recebimento de livros', 'ALTA', false, true, false)
ON CONFLICT DO NOTHING;

INSERT INTO configuracoes (id, filial_id, chave, descricao, valor, ativo, excluido)
VALUES (1, 1, 'emprestimo.dias.max', 'Max days for loan', '30', true, false),
       (2, 2, 'emprestimo.dias.max', 'Prazo maximo de emprestimo', '21', true, false),
       (3, 3, 'emprestimo.dias.max', 'Prazo maximo de emprestimo', '14', true, false),
       (4, 4, 'emprestimo.dias.max', 'Prazo maximo de emprestimo', '30', true, false),
       (5, 5, 'emprestimo.dias.max', 'Prazo maximo de emprestimo', '28', true, false),
       (6, 6, 'emprestimo.dias.max', 'Prazo maximo de emprestimo', '21', true, false)
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('filiais', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM filiais;
SELECT setval(pg_get_serial_sequence('editoras', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM editoras;
SELECT setval(pg_get_serial_sequence('generos', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM generos;
SELECT setval(pg_get_serial_sequence('autores', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM autores;
SELECT setval(pg_get_serial_sequence('usuarios', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM usuarios;
SELECT setval(pg_get_serial_sequence('estantes', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM estantes;
SELECT setval(pg_get_serial_sequence('materiais', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM materiais;
SELECT setval(pg_get_serial_sequence('exemplares', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM exemplares;
SELECT setval(pg_get_serial_sequence('emprestimos', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM emprestimos;
SELECT setval(pg_get_serial_sequence('historico_multas', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM historico_multas;
SELECT setval(pg_get_serial_sequence('lista_tarefas', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM lista_tarefas;
SELECT setval(pg_get_serial_sequence('configuracoes', 'id'), COALESCE(MAX(id), 1), MAX(id) IS NOT NULL) FROM configuracoes;
