CREATE OR REPLACE FUNCTION preencher_timestamps_auditoria()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        IF NEW.data_criacao IS NULL THEN
            NEW.data_criacao = CURRENT_TIMESTAMP;
        END IF;
        NEW.data_ultima_atualizacao = CURRENT_TIMESTAMP;
    ELSE
        IF NEW.data_criacao IS NULL THEN
            NEW.data_criacao = OLD.data_criacao;
        END IF;
        NEW.data_ultima_atualizacao = CURRENT_TIMESTAMP;
    END IF;

    RETURN NEW;
END;
$$;

UPDATE autores
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE configuracoes
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE editoras
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE emprestimos
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE estantes
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE exemplares
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE filiais
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE generos
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE gestores
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE historico_multas
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE lista_tarefas
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE materiais
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

UPDATE usuarios
SET data_criacao = COALESCE(data_criacao, CURRENT_TIMESTAMP),
    data_ultima_atualizacao = COALESCE(data_ultima_atualizacao, data_criacao, CURRENT_TIMESTAMP)
WHERE data_criacao IS NULL OR data_ultima_atualizacao IS NULL;

CREATE TRIGGER autores_preencher_timestamps
BEFORE INSERT OR UPDATE ON autores
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER configuracoes_preencher_timestamps
BEFORE INSERT OR UPDATE ON configuracoes
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER editoras_preencher_timestamps
BEFORE INSERT OR UPDATE ON editoras
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER emprestimos_preencher_timestamps
BEFORE INSERT OR UPDATE ON emprestimos
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER estantes_preencher_timestamps
BEFORE INSERT OR UPDATE ON estantes
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER exemplares_preencher_timestamps
BEFORE INSERT OR UPDATE ON exemplares
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER filiais_preencher_timestamps
BEFORE INSERT OR UPDATE ON filiais
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER generos_preencher_timestamps
BEFORE INSERT OR UPDATE ON generos
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER gestores_preencher_timestamps
BEFORE INSERT OR UPDATE ON gestores
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER historico_multas_preencher_timestamps
BEFORE INSERT OR UPDATE ON historico_multas
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER lista_tarefas_preencher_timestamps
BEFORE INSERT OR UPDATE ON lista_tarefas
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER materiais_preencher_timestamps
BEFORE INSERT OR UPDATE ON materiais
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();

CREATE TRIGGER usuarios_preencher_timestamps
BEFORE INSERT OR UPDATE ON usuarios
FOR EACH ROW EXECUTE FUNCTION preencher_timestamps_auditoria();
