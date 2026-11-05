UPDATE estantes
SET ativo = false
WHERE excluido = false
  AND (id, nome) IN (
      (1, 'Estante A1'),
      (2, 'Estante B1'),
      (3, 'Estante C1'),
      (4, 'Estante D1'),
      (5, 'Estante E1'),
      (6, 'Estante F1'),
      (7, 'Estante G1')
  );
