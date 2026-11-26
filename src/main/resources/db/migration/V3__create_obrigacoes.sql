CREATE TABLE obrigacoes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descricao VARCHAR(2000),
    prazo DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    empresa_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    criado_em TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    atualizado_em TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_obrigacoes_empresa FOREIGN KEY (empresa_id) REFERENCES empresas (id),
    CONSTRAINT fk_obrigacoes_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
);

CREATE INDEX idx_obrigacoes_empresa ON obrigacoes (empresa_id);
CREATE INDEX idx_obrigacoes_usuario ON obrigacoes (usuario_id);
CREATE INDEX idx_obrigacoes_status ON obrigacoes (status);
