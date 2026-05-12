ALTER TABLE tb_batch_allotments
    ADD COLUMN reserved_tickets INT NOT NULL DEFAULT 0;

ALTER TABLE tb_batch_allotments
    ADD CONSTRAINT chk_allotment_capacity
    CHECK (sold_tickets + reserved_tickets <= quota);