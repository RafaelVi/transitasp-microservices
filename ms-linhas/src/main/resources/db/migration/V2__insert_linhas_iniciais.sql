-- Carga inicial: as 13 linhas de metro e trem usadas no app TransitaSP (dados simulados).
-- A linha de id 3 comeca com velocidade reduzida porque existe uma ocorrencia aberta
-- para ela na carga inicial do ms-ocorrencias.
INSERT INTO tb_linha (id, numero, nome, tipo, cor_hex, status, atualizada_em) VALUES
    (1,  1,  'Azul',      'METRO', '#0455A1', 'NORMAL',              NOW()),
    (2,  2,  'Verde',     'METRO', '#007E5E', 'NORMAL',              NOW()),
    (3,  3,  'Vermelha',  'METRO', '#EE372F', 'VELOCIDADE_REDUZIDA', NOW()),
    (4,  4,  'Amarela',   'METRO', '#FFD400', 'NORMAL',              NOW()),
    (5,  5,  'Lilas',     'METRO', '#9B3894', 'NORMAL',              NOW()),
    (6,  7,  'Rubi',      'TREM',  '#CA016B', 'NORMAL',              NOW()),
    (7,  8,  'Diamante',  'TREM',  '#97A098', 'NORMAL',              NOW()),
    (8,  9,  'Esmeralda', 'TREM',  '#01A9A7', 'NORMAL',              NOW()),
    (9,  10, 'Turquesa',  'TREM',  '#008B8B', 'NORMAL',              NOW()),
    (10, 11, 'Coral',     'TREM',  '#F04E23', 'NORMAL',              NOW()),
    (11, 12, 'Safira',    'TREM',  '#083D8B', 'NORMAL',              NOW()),
    (12, 13, 'Jade',      'TREM',  '#00B352', 'NORMAL',              NOW()),
    (13, 15, 'Prata',     'METRO', '#9E9E93', 'NORMAL',              NOW());
