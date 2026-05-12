CREATE TABLE IF NOT EXISTS tb_order_status (
    status_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    CONSTRAINT uq_order_status_name UNIQUE (name)
);

INSERT INTO tb_order_status (status_id, name) VALUES
    (1, 'PENDING_PAYMENT'),
    (2, 'PAID'),
    (3, 'CANCELED'),
    (4, 'EXPIRED'),
    (5, 'REFUNDED')
ON CONFLICT (name) DO NOTHING;

-- Ajusta a sequence para não colidir com IDs já inseridos.
-- Sem isso, o próximo INSERT autogerado tentaria status_id=1 e falharia.
SELECT setval(
    pg_get_serial_sequence('tb_order_status', 'status_id'),
    (SELECT MAX(status_id) FROM tb_order_status)
);