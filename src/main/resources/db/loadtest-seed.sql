-- ============================================================
-- loadtest-seed.sql — dataset controlado para testes de carga (perfil loadtest).
-- Flyway já criou schema + referência (roles/status/tipos). Aqui: org, venue,
-- evento (espelho do MVP Allianz, com lotes calibrados) e N clientes.
-- Assume DB limpo (run.sh faz `docker compose down -v` antes).
-- Datas LocalDateTime usam AT TIME ZONE 'America/Sao_Paulo' (ver nota de fuso).
-- ============================================================
TRUNCATE TABLE
    tb_tickets,
    tb_order_items,
    tb_orders,
    tb_payment_events,
    tb_idempotency_keys,
    tb_event_demand_profiles,
    tb_batch_allotments,
    tb_ticket_batches,
    tb_event_date_sectors,
    tb_event_dates,
    tb_event_sectors,
    tb_event_images,
    tb_events,
    tb_venue_sectors,
    tb_venues
RESTART IDENTITY CASCADE;

-- ── Organizer (user + organizer) ──
INSERT INTO tb_users (user_id, email, password, role_id, status_id)
VALUES ('b0000000-0000-0000-0000-000000000001', 'organizer-lt@goticket.dev', '$2a$10$HbhkSnFJf8VDEaWKPMumLOU.aa6ool.ShfANHTyl/xHDOJUd/hm2W', 2, 1)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO tb_organizers (user_id, organizer_name, legal_name, cnpj, register_date, last_update_date)
VALUES ('b0000000-0000-0000-0000-000000000001', 'LoadTest Eventos', 'LoadTest Eventos Ltda', '00000000000100', NOW(), NOW())
ON CONFLICT (user_id) DO NOTHING;

-- ── Venue (espelho do Allianz) + setores ──
INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number,
    neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES ('Allianz Parque [LT]', 'Arena Multiuso S.A. [LT]', '00000000000200',
    'Venue espelho para teste de carga.', 'Av. Francisco Matarazzo', '1705', 'Água Branca',
    'São Paulo', 'SP', 'Brasil', '05001200', NOW(), NOW(), NOW(), 1,
    'b0000000-0000-0000-0000-000000000001');

INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
SELECT s.name, s.descr, s.cap, NOW(), NOW(), v.venue_id
FROM (SELECT venue_id FROM tb_venues WHERE name = 'Allianz Parque [LT]') v
CROSS JOIN (VALUES
    ('Pista Premium', 'Setor pequeno — alvo do teste de overselling', 5000),
    ('Pista',         'Setor grande — alvo de disponibilidade/jornada', 15000)
) AS s(name, descr, cap);

-- ── Evento (espelho do MVP). expected_high_demand=FALSE: o tier é forçado
--    por cenário no run.sh (NORMAL p/ A/B; HIGH p/ C/E). sales aberto. ──
INSERT INTO tb_events (title, description, age_restriction, sales_start_date, start_date, end_date,
    approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id,
    expected_high_demand)
SELECT 'Allianz Live Experience [LT]', 'Evento espelho para teste de carga.', 18,
    (NOW() AT TIME ZONE 'America/Sao_Paulo') - INTERVAL '1 day',     -- vendas ABERTAS
    (NOW() AT TIME ZONE 'America/Sao_Paulo') + INTERVAL '30 days',   -- evento no futuro
    (NOW() AT TIME ZONE 'America/Sao_Paulo') + INTERVAL '30 days 6 hours',
    NOW(), NOW(), NOW(), 9, 2, 'b0000000-0000-0000-0000-000000000001', 1, v.venue_id, FALSE
FROM tb_venues v WHERE v.name = 'Allianz Parque [LT]';

-- ── 1 data ──
INSERT INTO tb_event_dates (start_date, end_date, register_date, last_update_date, status_id, event_id)
SELECT (NOW() AT TIME ZONE 'America/Sao_Paulo') + INTERVAL '30 days',
       (NOW() AT TIME ZONE 'America/Sao_Paulo') + INTERVAL '30 days 6 hours',
       NOW(), NOW(), 2, e.event_id
FROM tb_events e WHERE e.title = 'Allianz Live Experience [LT]';

-- ── 2 event sectors (ligados aos venue sectors) ──
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
SELECT vs.name, vs.description, NOW(), NOW(), FALSE, e.event_id, vs.sector_id
FROM tb_events e
JOIN tb_venues v ON v.venue_id = e.venue_id
JOIN tb_venue_sectors vs ON vs.venue_id = v.venue_id
WHERE e.title = 'Allianz Live Experience [LT]';

-- ── event_date_sectors (1 data × 2 setores) ──
INSERT INTO tb_event_date_sectors (register_date, last_update_date, event_date_id, event_sector_id)
SELECT NOW(), NOW(), ed.event_date_id, es.sector_id
FROM tb_events e
JOIN tb_event_dates ed ON ed.event_id = e.event_id
JOIN tb_event_sectors es ON es.event_id = e.event_id
WHERE e.title = 'Allianz Live Experience [LT]';

-- ── Lotes: Pista Premium = 200 (ESTOURA → cenário A); Pista = 12000 (não esgota → C/E) ──
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, event_date_sector_id)
SELECT 1,
       CASE es.name WHEN 'Pista Premium' THEN 350.00 ELSE 200.00 END,
       CASE es.name WHEN 'Pista Premium' THEN 200     ELSE 12000 END,
       eds.event_date_sector_id
FROM tb_event_date_sectors eds
JOIN tb_event_sectors es ON es.sector_id = eds.event_sector_id
JOIN tb_events e ON e.event_id = es.event_id
WHERE e.title = 'Allianz Live Experience [LT]';

-- ── Allotments: 50% FULL / 40% HALF / 10% SOLIDARY (soma exata) ──
INSERT INTO tb_batch_allotments (ticket_type_id, quota, sold_tickets, price, register_date, last_update_date, batch_id)
SELECT tt.id,
       CASE tt.id
            WHEN 1 THEN tb.total_tickets * 50 / 100
            WHEN 2 THEN tb.total_tickets * 40 / 100
            ELSE tb.total_tickets - (tb.total_tickets*50/100) - (tb.total_tickets*40/100)
       END,
       0,
       CASE WHEN tt.id = 3 THEN tb.price ELSE NULL END,   -- SOLIDARY exige price
       NOW(), NOW(), tb.batch_id
FROM tb_ticket_batches tb
JOIN tb_event_date_sectors eds ON eds.event_date_sector_id = tb.event_date_sector_id
JOIN tb_event_sectors es ON es.sector_id = eds.event_sector_id
JOIN tb_events e ON e.event_id = es.event_id
CROSS JOIN (VALUES (1),(2),(3)) AS tt(id)
WHERE e.title = 'Allianz Live Experience [LT]';

-- ── N clientes para o k6 (user + client) via generate_series ──
INSERT INTO tb_users (user_id, email, password, role_id, status_id)
SELECT ('00000000-0000-0000-0000-' || lpad(g::text, 12, '0'))::uuid,
       'loadtest+' || g || '@goticket.dev', '$2a$10$HbhkSnFJf8VDEaWKPMumLOU.aa6ool.ShfANHTyl/xHDOJUd/hm2W', 3, 1
FROM generate_series(1, 2000) g
ON CONFLICT (email) DO NOTHING;

INSERT INTO tb_clients (user_id, full_name, sex, identity_document, birth_date, register_date, last_update_date)
SELECT ('00000000-0000-0000-0000-' || lpad(g::text, 12, '0'))::uuid,
       'LoadTest Cliente ' || g, 1, lpad(g::text, 11, '0'),
       '1990-01-01', NOW(), NOW()
FROM generate_series(1, 2000) g
ON CONFLICT (user_id) DO NOTHING;