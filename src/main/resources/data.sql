-- Populando Roles
INSERT INTO tb_roles (role_id, name) VALUES (1, 'ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (2, 'ORGANIZER') ON CONFLICT DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (3, 'CLIENT') ON CONFLICT DO NOTHING;

-- Populando User Status
INSERT INTO tb_user_status (status_id, name) VALUES (1, 'ACTIVE') ON CONFLICT DO NOTHING;
INSERT INTO tb_user_status (status_id, name) VALUES (2, 'INACTIVE') ON CONFLICT DO NOTHING;

-- Populando Event Status
INSERT INTO tb_event_status (status_id, name) VALUES (1, 'PENDING_APPROVAL') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (2, 'APPROVED') ON CONFLICT DO NOTHING;
INSERT INTO tb_user_status (status_id, name) VALUES (3, 'COMPLETED') ON CONFLICT DO NOTHING;
INSERT INTO tb_user_status (status_id, name) VALUES (4, 'DECLINED') ON CONFLICT DO NOTHING;
INSERT INTO tb_user_status (status_id, name) VALUES (5, 'CANCELED') ON CONFLICT DO NOTHING;
INSERT INTO tb_user_status (status_id, name) VALUES (6, 'POSTPONED') ON CONFLICT DO NOTHING;

-- Populando Users (Organizer)
INSERT INTO tb_users (user_id, email, password, role_id, status_id)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'organizer@events.com', '$2a$10$3zHzb.NpvO.He.WeQ3.p2.o.j.m1G.m2vQ.EaG.g/s5.f.b1G.m2', 2, 1)
ON CONFLICT (user_id) DO NOTHING;

-- Populando Organizer
INSERT INTO tb_organizers (user_id, organizer_name, legal_name, cnpj, register_date, last_update_date)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Mega Eventos', 'Mega Eventos Ltda.', '54550651000110', NOW(), NOW())
ON CONFLICT (user_id) DO NOTHING;

-- Populando Event Visibilities
INSERT INTO tb_event_visibilities (visibility_id, name) VALUES (1, 'PUBLIC') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_visibilities (visibility_id, name) VALUES (2, 'PRIVATE') ON CONFLICT DO NOTHING;

-- Populando Events
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Rock Fest 2025', 'O maior festival de rock do ano.', 18, '2025-12-01 18:00:00', '2025-12-02 02:00:00', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 2);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Spring Boot Conf', 'Conferência avançada de Spring Boot.', 16, '2025-11-20 09:00:00', '2025-11-20 18:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Festival Gastronômico', 'Sabores de todo o mundo.', 0, '2025-11-25 12:00:00', '2025-11-25 22:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Maratona Tech', 'Hackathon 48h para desenvolvedores.', 18, '2025-12-05 19:00:00', '2025-12-07 19:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Concerto de Jazz', 'Noite de Jazz e Blues ao vivo.', 14, '2025-11-28 20:00:00', '2025-11-28 23:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Expo Arte Digital', 'Exposição de arte e novas mídias.', 0, '2025-12-10 10:00:00', '2025-12-15 20:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Samba de Raiz', 'Roda de samba tradicional.', 18, '2025-11-22 16:00:00', '2025-11-22 21:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Feira de Livros', 'Grande feira de livros com autores convidados.', 0, '2026-01-15 09:00:00', '2026-01-18 18:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Show de Comédia Stand-up', 'Os melhores comediantes da atualidade.', 16, '2025-11-29 21:00:00', '2025-11-29 23:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Palestra IA', 'O futuro da Inteligência Artificial.', 12, '2025-12-03 19:00:00', '2025-12-03 22:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Aula de Yoga no Parque', 'Sessão de yoga ao ar livre.', 0, '2025-11-23 08:00:00', '2025-11-23 09:30:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Circuito de Corrida', 'Corrida de rua 10km.', 16, '2025-12-14 07:00:00', '2025-12-14 10:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Festival de Cinema Indie', 'Mostra de filmes independentes.', 14, '2026-01-20 14:00:00', '2026-01-25 23:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Torneio de E-Sports', 'Competição de LoL e CS.', 16, '2025-12-20 10:00:00', '2025-12-21 22:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Bazar de Natal', 'Artesanato e comidas típicas de natal.', 0, '2025-12-12 11:00:00', '2025-12-13 19:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Workshop de Fotografia', 'Aprenda a fotografar com mestres.', 18, '2026-02-05 14:00:00', '2026-02-05 18:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Festa Eletrônica', 'DJs internacionais.', 18, '2025-12-27 22:00:00', '2025-12-28 06:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Peça de Teatro: O Fantasma', 'Clássico do teatro mundial.', 12, '2026-03-10 20:00:00', '2026-03-10 23:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Show Acústico', 'Voz e violão com artistas locais.', 12, '2025-11-30 19:00:00', '2025-11-30 21:00:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, status_id, organizer_id, event_visibility)
VALUES ('Palestra sobre Investimentos', 'Como investir em 2026.', 18, '2026-01-10 19:30:00', '2026-01-10 21:30:00', NOW(), NOW(), NOW(), 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1);