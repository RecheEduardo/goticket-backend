-- ============================================================
-- V2 — Seed de dados de referência (domínio fixo)
-- Aplicado pelo Flyway em todos os ambientes (dev, loadtest, prod).
-- Mantém IDs estáveis para FKs e enums espelhados em Java.
-- ============================================================

-- Roles
INSERT INTO tb_roles (role_id, name) VALUES (1, 'ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (2, 'ORGANIZER') ON CONFLICT DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (3, 'CLIENT') ON CONFLICT DO NOTHING;

-- Status (usuários, venues, etc.)
INSERT INTO tb_status (status_id, name) VALUES (1, 'ACTIVE') ON CONFLICT DO NOTHING;
INSERT INTO tb_status (status_id, name) VALUES (2, 'INACTIVE') ON CONFLICT DO NOTHING;

-- Event Status
INSERT INTO tb_event_status (status_id, name) VALUES (1, 'PENDING_APPROVAL') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (2, 'APPROVED') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (3, 'COMPLETED') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (4, 'DECLINED') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (5, 'CANCELED') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (6, 'POSTPONED') ON CONFLICT DO NOTHING;

-- Event Visibilities
INSERT INTO tb_event_visibilities (visibility_id, name) VALUES (1, 'PUBLIC') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_visibilities (visibility_id, name) VALUES (2, 'PRIVATE') ON CONFLICT DO NOTHING;

-- Tipos de ingresso
INSERT INTO tb_ticket_types (ticket_type_id, name) VALUES (1, 'FULL')     ON CONFLICT DO NOTHING;
INSERT INTO tb_ticket_types (ticket_type_id, name) VALUES (2, 'HALF')     ON CONFLICT DO NOTHING;
INSERT INTO tb_ticket_types (ticket_type_id, name) VALUES (3, 'SOLIDARY') ON CONFLICT DO NOTHING;

-- Tipos de elegibilidade (sub-tipos de meia)
INSERT INTO tb_eligibility_types (eligibility_type_id, name) VALUES (1, 'STUDENT')          ON CONFLICT DO NOTHING;
INSERT INTO tb_eligibility_types (eligibility_type_id, name) VALUES (2, 'ELDERLY')          ON CONFLICT DO NOTHING;
INSERT INTO tb_eligibility_types (eligibility_type_id, name) VALUES (3, 'DISABILITY')       ON CONFLICT DO NOTHING;
INSERT INTO tb_eligibility_types (eligibility_type_id, name) VALUES (4, 'LOW_INCOME_YOUTH') ON CONFLICT DO NOTHING;
INSERT INTO tb_eligibility_types (eligibility_type_id, name) VALUES (5, 'TEACHER')          ON CONFLICT DO NOTHING;

-- Status do ingresso
INSERT INTO tb_ticket_status (ticket_status_id, name) VALUES (1, 'ACTIVE')      ON CONFLICT DO NOTHING;
INSERT INTO tb_ticket_status (ticket_status_id, name) VALUES (2, 'USED')        ON CONFLICT DO NOTHING;
INSERT INTO tb_ticket_status (ticket_status_id, name) VALUES (3, 'CANCELED')    ON CONFLICT DO NOTHING;
INSERT INTO tb_ticket_status (ticket_status_id, name) VALUES (4, 'REFUNDED')    ON CONFLICT DO NOTHING;
INSERT INTO tb_ticket_status (ticket_status_id, name) VALUES (5, 'TRANSFERRED') ON CONFLICT DO NOTHING;

-- Event Categories
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (1,  'Música',               'musica')               ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (2,  'Tecnologia',           'tecnologia')           ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (3,  'Gastronomia',          'gastronomia')          ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (4,  'Artes e Cultura',      'artes-e-cultura')      ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (5,  'Cursos e Workshops',   'cursos-e-workshops')   ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (6,  'Esportes e Bem-estar', 'esportes-e-bem-estar') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (7,  'Comédia e Stand-up',   'comedia-e-stand-up')   ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (8,  'Feiras e Negócios',    'feiras-e-negocios')    ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (9,  'Festas e Shows',       'festas-e-shows')       ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (10, 'Games e E-Sports',     'games-e-esports')      ON CONFLICT DO NOTHING;