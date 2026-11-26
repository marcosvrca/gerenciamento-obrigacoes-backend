CREATE TABLE anexos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_original VARCHAR(255) NOT NULL,
    content_type VARCHAR(150) NOT NULL,
    caminho_armazenado VARCHAR(500) NOT NULL,
    tamanho_bytes BIGINT NOT NULL,
    obrigacao_id BIGINT NOT NULL,
    criado_em TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_anexos_obrigacao FOREIGN KEY (obrigacao_id) REFERENCES obrigacoes (id)
);

CREATE INDEX idx_anexos_obrigacao ON anexos (obrigacao_id);
