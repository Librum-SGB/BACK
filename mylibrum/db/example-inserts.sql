-- Example INSERT statements for MyLibrum database (Postgres-style)
-- Run in a dev environment only. Adjust sequences and timestamps as needed.

BEGIN;

-- FILIAIS
INSERT INTO filiais (id, nome_fantasia, razao_social, endereco, cidade, bairro, estado, cep, cnpj, email, numero, telefone, inscricao_estadual, complemento, ativo, excluido)
VALUES (1, 'Biblioteca Central', 'Biblioteca Municipal Central Ltda', 'Av. Principal, 100', 'Cidade Exemplo', 'Centro', 'SP', '01000000', '12345678000100', 'contato@biblioteca.ex', '100', '11999990000', '123456', NULL, true, false);

-- EDITORAS
INSERT INTO editoras (id, nome, nacionalidade, ativo, excluido)
VALUES (1, 'Editora Exemplo', 'Brasil', true, false);

-- GENEROS
INSERT INTO generos (id, nome, descricao, ativo, excluido)
VALUES (1, 'Ficcao', 'Obras de ficção geral', true, false),
       (2, 'Historia', 'Livros de história', true, false);

-- AUTORES
INSERT INTO autores (id, nome, nacionalidade, biografia, data_nascimento, ativo, excluido)
VALUES (1, 'Joao Silva', 'Brasil', 'Autor exemplo.', '1970-05-12', true, false),
       (2, 'Maria Souza', 'Brasil', 'Outra autora exemplo.', '1980-08-20', true, false);

-- USUARIOS
INSERT INTO usuarios (id, nome, cpf, telefone, email, data_nascimento, filial_id, ativo, excluido)
VALUES (1, 'Aluno Exemplo', '12345678901', '11988887777', 'aluno@exemplo.com', '1995-03-10', 1, true, false);

-- GESTORES -- senha: senhaTeste
INSERT INTO gestores (id, login, senha, matricula_funcionario, filial_id, ultimo_acesso, ativo, excluido)
VALUES (1, 'gestor1', '$2a$10$ok.f.ukSnJNPiqpRZ0xExeHWEWkpdnNo2tegG.HoMvCbpzzl4ktPa', 'MAT123', 1, now(), true, false);

-- ESTANTES
INSERT INTO estantes (id, localizacao, capacidade, ativo, excluido)
VALUES (1, 'A1', 100, true, false),
       (2, 'B1', 80, true, false);

-- MATERIAIS (observação: coluna autor_id é relação ManyToOne; também populamos materiais_autores)
INSERT INTO materiais (id, titulo, tipo, subtitulo, sinopse, issn, tema, descricao, isbn, edicao, ano_publicacao, quantidade_paginas, editora_id, autor_id, ativo, excluido)
VALUES (1, 'Aprendendo Java', 'LIVRO', 'Uma introducao', 'Sinopse do livro.', NULL, 'Programacao', 'Descricao longa do livro.', '9781234567897', 1, 2020, 350, 1, 1, true, false),
       (2, 'Revista Exemplo', 'PERIODICO', NULL, 'Sinopse da revista.', '1234-5678', 'Ciencia', 'Descricao da revista.', '0001234560000', 1, 2021, 40, 1, 2, true, false);

-- ASSOCIACOES MATERIAIS_AUTOR and MATERIAIS_GENEROS
INSERT INTO materiais_autores (autor_id, material_id) VALUES (1,1), (2,2);
INSERT INTO materiais_generos (genero_id, material_id) VALUES (1,1), (2,2);

-- EXEMPLARES
INSERT INTO exemplares (id, material_id, filial_id, estante_id, prateleira, posicao, codigo_barras, status, data_aquisicao, observacoes, ativo, excluido)
VALUES (1, 1, 1, 1, 'P1', '01', 'CB-0001', 'DISPONIVEL', '2022-01-10', 'Exemplar em bom estado', true, false),
       (2, 2, 1, 2, 'P2', '05', 'CB-0002', 'DISPONIVEL', '2022-02-15', NULL, true, false);

-- EMPRESTIMOS
INSERT INTO emprestimos (id, exemplar_id, gestor_id, usuario_id, data_saida, data_devolucao_prevista, renovacoes_contagem, data_devolucao_efetivada, ativo, excluido)
VALUES (1, 1, 1, 1, now()-interval '10 days', now()+interval '20 days', 0, NULL, true, false);

-- HISTORICO_MULTAS (relacionado ao emprestimo)
INSERT INTO historico_multas (id, emprestimo_id, dias_atraso, valor, pago, data_pagamento, ativo, excluido)
VALUES (1, 1, 0, 0.00, false, NULL, true, false);

-- LISTA_TAREFAS
INSERT INTO lista_tarefas (id, gestor_id, descricao, prioridade, concluida, ativo, excluido)
VALUES (1, 1, 'Reorganizar estantes da seção A', 'MEDIA', false, true, false);

-- CONFIGURACOES (chave unica por filial)
INSERT INTO configuracoes (id, filial_id, chave, descricao, valor, ativo, excluido)
VALUES (1, 1, 'emprestimo.dias.max', 'Max days for loan', '30', true, false);

COMMIT;

-- Notes:
-- - Adjust sequence values (e.g., ALTER SEQUENCE ... RESTART WITH ...) after inserting explicit ids if using serial/identity columns.
-- - Passwords above are placeholders; replace with properly hashed values for real use.
-- - The script assumes tables and constraints already exist. Remove or adapt `id` explicit values if your DB uses automatic identities.
