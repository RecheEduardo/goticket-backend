ALTER TABLE tb_events ADD COLUMN expected_high_demand BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE tb_event_demand_profiles (
    event_id BIGINT PRIMARY KEY REFERENCES tb_events(event_id) ON DELETE CASCADE,
    tier VARCHAR(20) NOT NULL DEFAULT 'NORMAL',          -- NORMAL | HIGH
    source VARCHAR(20) NOT NULL DEFAULT 'AUTO',           -- AUTO | MANUAL | SCHEDULED
    sales_velocity_per_min NUMERIC(8,2),
    occupancy_rate NUMERIC(5,4),
    last_evaluated_at TIMESTAMP NOT NULL,
    manual_override_until TIMESTAMP
);