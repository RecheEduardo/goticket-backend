-- V6 — Chaves de idempotência
-- Garante que POST /orders com mesma Idempotency-Key retorne
-- a Order original em vez de criar nova.
-- TTL: 24h (limpeza via job).

CREATE TABLE IF NOT EXISTS tb_idempotency_keys (
    key VARCHAR(80) PRIMARY KEY,

    user_id UUID NOT NULL,
    endpoint VARCHAR(80) NOT NULL,
    request_hash VARCHAR(64) NOT NULL,

    response_status INTEGER,
    response_body TEXT,

    order_id BIGINT,

    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_idempotency_user
        FOREIGN KEY (user_id)  REFERENCES tb_users(user_id),
    CONSTRAINT fk_idempotency_order
        FOREIGN KEY (order_id) REFERENCES tb_orders(order_id) ON DELETE SET NULL,

    CONSTRAINT chk_idempotency_expires_after_created
        CHECK (expires_at > created_at)
);

CREATE INDEX IF NOT EXISTS ix_idempotency_expires ON tb_idempotency_keys(expires_at);

CREATE INDEX IF NOT EXISTS ix_idempotency_user_endpoint
    ON tb_idempotency_keys(user_id, endpoint);