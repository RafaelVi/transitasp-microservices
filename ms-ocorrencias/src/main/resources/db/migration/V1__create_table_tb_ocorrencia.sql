-- linha_id guarda o id da linha no ms-linhas. Nao ha chave estrangeira: os bancos sao independentes.
CREATE TABLE IF NOT EXISTS tb_ocorrencia (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    linha_id         BIGINT       NOT NULL,
    tipo             VARCHAR(20)  NOT NULL,
    descricao        VARCHAR(255) NOT NULL,
    local_ocorrencia VARCHAR(80)  NOT NULL,
    impacto          VARCHAR(20)  NOT NULL,
    status           VARCHAR(10)  NOT NULL,
    aberta_em        DATETIME     NOT NULL,
    encerrada_em     DATETIME     NULL,
    CONSTRAINT pk_tb_ocorrencia PRIMARY KEY (id)
);
