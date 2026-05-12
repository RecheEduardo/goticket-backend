-- V7 — Eventos da Stripe (webhooks)
-- Cada webhook recebido é gravado AQUI antes de qualquer
-- processamento. UNIQUE (stripe_event_id) garante idempotência
-- contra entrega duplicada (at-least-once delivery da Stripe).

CREATE TABLE IF NOT EXISTS tb_payment_events (
    payment_event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    stripe_event_id VARCHAR(80) NOT NULL,
    type VARCHAR(80) NOT NULL,
    payment_intent_id VARCHAR(80),

    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP,

    -- Payload bruto recebido da Stripe (assinatura já validada).
    -- Mantido como TEXT no D1 para evitar dependência extra.
    -- Migração futura para JSONB: ALTER TABLE ... USING payload_json::jsonb
    payload_json TEXT NOT NULL,

    error_message VARCHAR(500),

    CONSTRAINT uq_payment_events_stripe_event_id UNIQUE (stripe_event_id)
);

CREATE INDEX IF NOT EXISTS ix_payment_events_intent
    ON tb_payment_events(payment_intent_id);

CREATE INDEX IF NOT EXISTS ix_payment_events_unprocessed
    ON tb_payment_events(received_at)
    WHERE processed_at IS NULL;