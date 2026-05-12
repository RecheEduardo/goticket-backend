CREATE TABLE IF NOT EXISTS tb_orders (
    order_id bigint PRIMARY KEY,
    idempotency_key VARCHAR(80) NOT NULL,
    buyer_id UUID NOT NULL,
    event_id BIGINT NOT NULL,
    event_date_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    subtotal NUMERIC(12,2) NOT NULL,
    fees_total NUMERIC(12,2) NOT NULL,
    total_price NUMERIC(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'BRL',
    payment_provider VARCHAR(20) NOT NULL DEFAULT 'STRIPE',
    payment_intent_id VARCHAR(80),
    payment_method_snapshot VARCHAR(80),
    placed_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    paid_at TIMESTAMP,
    canceled_at TIMESTAMP,
    cancel_reason VARCHAR(200),
    refunded_at TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_orders_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT uq_orders_payment_intent_id UNIQUE (payment_intent_id),

    CONSTRAINT fk_orders_buyer
        FOREIGN KEY (buyer_id)        REFERENCES tb_users(user_id),
    CONSTRAINT fk_orders_event
        FOREIGN KEY (event_id)        REFERENCES tb_events(event_id),
    CONSTRAINT fk_orders_event_date
        FOREIGN KEY (event_date_id)   REFERENCES tb_event_dates(event_date_id),
    CONSTRAINT fk_orders_status
        FOREIGN KEY (status_id)       REFERENCES tb_order_status(status_id),

    -- Sanidade: total_price = subtotal + fees_total
    CONSTRAINT chk_orders_total_consistency
        CHECK (total_price = subtotal + fees_total),

    -- TTL coerente
    CONSTRAINT chk_orders_expires_after_placed
        CHECK (expires_at > placed_at)
);

CREATE INDEX IF NOT EXISTS ix_orders_payment_intent ON tb_orders(payment_intent_id);
CREATE INDEX IF NOT EXISTS ix_orders_buyer         ON tb_orders(buyer_id);
CREATE INDEX IF NOT EXISTS ix_orders_expires_at    ON tb_orders(expires_at);

-- Índice parcial: apenas orders pendentes.
CREATE INDEX IF NOT EXISTS ix_orders_pending_expires
    ON tb_orders(expires_at)
    WHERE status_id = 1;

CREATE TABLE IF NOT EXISTS tb_order_items (
    order_item_id bigint PRIMARY KEY,

    order_id BIGINT NOT NULL,
    batch_allotment_id BIGINT NOT NULL,
    ticket_type_id BIGINT NOT NULL,

    holder_name VARCHAR(200) NOT NULL,
    holder_document VARCHAR(20) NOT NULL,

    eligibility_type_id BIGINT,
    eligibility_document_number VARCHAR(50),

    unit_price NUMERIC(10,2) NOT NULL,
    fee_amount NUMERIC(10,2) NOT NULL,

    ticket_id UUID,

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)            REFERENCES tb_orders(order_id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_allotment
        FOREIGN KEY (batch_allotment_id)  REFERENCES tb_batch_allotments(allotment_id),
    CONSTRAINT fk_order_items_ticket_type
        FOREIGN KEY (ticket_type_id)      REFERENCES tb_ticket_types(ticket_type_id),
    CONSTRAINT fk_order_items_eligibility
        FOREIGN KEY (eligibility_type_id) REFERENCES tb_eligibility_types(eligibility_type_id),
    CONSTRAINT fk_order_items_ticket
        FOREIGN KEY (ticket_id)           REFERENCES tb_tickets(ticket_id),

    CONSTRAINT uq_order_items_ticket UNIQUE (ticket_id),

    CONSTRAINT chk_order_items_unit_price_non_negative CHECK (unit_price >= 0),
    CONSTRAINT chk_order_items_fee_non_negative       CHECK (fee_amount >= 0)
);

CREATE INDEX IF NOT EXISTS ix_order_items_order     ON tb_order_items(order_id);
CREATE INDEX IF NOT EXISTS ix_order_items_allotment ON tb_order_items(batch_allotment_id);