-- Carga inicial (dados simulados).
-- A ocorrencia aberta da linha de id 3 (Linha 3 - Vermelha) e o motivo de essa linha
-- comecar com status VELOCIDADE_REDUZIDA na carga inicial do ms-linhas.
INSERT INTO tb_ocorrencia (id, linha_id, tipo, descricao, local_ocorrencia, impacto, status, aberta_em, encerrada_em) VALUES
    (1, 3, 'FALHA_TECNICA', 'Falha em equipamento de via, trens circulando com velocidade reduzida',
        'Entre Se e Bras', 'VELOCIDADE_REDUZIDA', 'ABERTA', NOW(), NULL),
    (2, 8, 'OBJETO_NA_VIA', 'Objeto na via retirado pela equipe de manutencao',
        'Estacao Pinheiros', 'PARALISADA', 'ENCERRADA', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 23 HOUR)),
    (3, 1, 'MANUTENCAO', 'Manutencao programada concluida',
        'Estacao Jabaquara', 'VELOCIDADE_REDUZIDA', 'ENCERRADA', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 46 HOUR));
