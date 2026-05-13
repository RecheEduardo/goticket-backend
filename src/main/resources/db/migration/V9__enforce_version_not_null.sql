-- Garante que @Version nunca seja null nas tabelas com lock otimista.
-- Aplica defesa em profundidade: tanto a coluna NOT NULL quanto o DEFAULT 0.

UPDATE tb_batch_allotments SET version = 0 WHERE version IS NULL;
ALTER TABLE tb_batch_allotments
    ALTER COLUMN version SET DEFAULT 0,
    ALTER COLUMN version SET NOT NULL;

UPDATE tb_ticket_batches SET version = 0 WHERE version IS NULL;
ALTER TABLE tb_ticket_batches
    ALTER COLUMN version SET DEFAULT 0,
    ALTER COLUMN version SET NOT NULL;