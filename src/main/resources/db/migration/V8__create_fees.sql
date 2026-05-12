-- V8 — Configuração de taxas (tarifas da plataforma/organizador/evento)
-- Cada Fee tem escopo (PLATFORM > ORGANIZER > EVENT) e vigência
-- temporal. FeeCalculator resolve a Fee aplicável no momento da
-- compra e PERSISTE o valor calculado em OrderItem.fee_amount
-- (snapshot — taxas mudam, Orders passadas não.)

CREATE TABLE IF NOT EXISTS tb_fees (
    fee_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    name VARCHAR(80) NOT NULL,
    description VARCHAR(200),

    fee_type VARCHAR(20) NOT NULL,
    value NUMERIC(10,4) NOT NULL,

    scope VARCHAR(20) NOT NULL,
    scope_ref_id BIGINT,

    applies_to VARCHAR(20) NOT NULL,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    effective_from TIMESTAMP NOT NULL,
    effective_to TIMESTAMP,            -- null = vigente

    register_date TIMESTAMP NOT NULL,
    last_update_date TIMESTAMP NOT NULL,

    CONSTRAINT chk_fees_fee_type
        CHECK (fee_type IN ('PERCENT', 'FIXED')),
    CONSTRAINT chk_fees_scope
        CHECK (scope IN ('PLATFORM', 'ORGANIZER', 'EVENT')),
    CONSTRAINT chk_fees_applies_to
        CHECK (applies_to IN ('PER_TICKET', 'PER_ORDER')),

    CONSTRAINT chk_fees_scope_ref
        CHECK (
            (scope = 'PLATFORM'  AND scope_ref_id IS NULL) OR
            (scope IN ('ORGANIZER', 'EVENT') AND scope_ref_id IS NOT NULL)
        ),

    CONSTRAINT chk_fees_value_non_negative
        CHECK (value >= 0),

    CONSTRAINT chk_fees_percent_range
        CHECK (fee_type <> 'PERCENT' OR value <= 1),

    CONSTRAINT chk_fees_effective_range
        CHECK (effective_to IS NULL OR effective_to > effective_from)
);

CREATE INDEX IF NOT EXISTS ix_fees_scope
    ON tb_fees(scope, scope_ref_id, is_active);

-- Acelera a query "quais fees estão vigentes agora?" do FeeCalculator
CREATE INDEX IF NOT EXISTS ix_fees_active_vigent
    ON tb_fees(scope, scope_ref_id, effective_from, effective_to)
    WHERE is_active = TRUE;

-- ============================================================
-- Seed mínimo: 1 taxa de plataforma padrão (5%, PER_TICKET).
-- Pode ser ajustada via UPDATE no dev. Não é "dado de domínio
-- fixo" — está aqui para o D2/D4 terem algo a calcular sem
-- precisar de UI.
-- ============================================================
INSERT INTO tb_fees (
    name, description, fee_type, value, scope, scope_ref_id, applies_to,
    is_active, effective_from, register_date, last_update_date
) VALUES (
    'Taxa de plataforma',
    'Taxa padrão aplicada por ingresso (configurável).',
    'PERCENT',
    0.0500,
    'PLATFORM',
    NULL,
    'PER_TICKET',
    TRUE,
    NOW(),
    NOW(),
    NOW()
);