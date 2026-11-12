CREATE TABLE IF NOT EXISTS tb_linha (
    id            BIGINT      NOT NULL AUTO_INCREMENT,
    numero        INT         NOT NULL,
    nome          VARCHAR(60) NOT NULL,
    tipo          VARCHAR(10) NOT NULL,
    cor_hex       VARCHAR(7)  NOT NULL,
    status        VARCHAR(20) NOT NULL,
    atualizada_em DATETIME    NOT NULL,
    CONSTRAINT pk_tb_linha PRIMARY KEY (id),
    CONSTRAINT uk_tb_linha_numero UNIQUE (numero)
);
