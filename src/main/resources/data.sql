-- Populando Roles
INSERT INTO tb_roles (role_id, name) VALUES (1, 'ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (2, 'ORGANIZER') ON CONFLICT DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (3, 'CLIENT') ON CONFLICT DO NOTHING;

-- Populando Status
INSERT INTO tb_status (status_id, name) VALUES (1, 'ACTIVE') ON CONFLICT DO NOTHING;
INSERT INTO tb_status (status_id, name) VALUES (2, 'INACTIVE') ON CONFLICT DO NOTHING;

-- Populando Event Status
INSERT INTO tb_event_status (status_id, name) VALUES (1, 'PENDING_APPROVAL') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (2, 'APPROVED') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (3, 'COMPLETED') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (4, 'DECLINED') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (5, 'CANCELED') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_status (status_id, name) VALUES (6, 'POSTPONED') ON CONFLICT DO NOTHING;

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

-- Populando Venues (Locais de Eventos)
INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Espaço das Américas', 'Espaço das Américas Eventos Ltda', '12345678000199', 'Tradicional casa de shows e eventos coberta.', 'Rua Tagipuru', '795', 'Barra Funda', 'São Paulo', 'SP', 'Brasil', '01156000', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Allianz Parque', 'Arena Multiuso S.A.', '98765432000111', 'Estádio multiuso com capacidade para grandes shows internacionais.', 'Avenida Francisco Matarazzo', '1705', 'Água Branca', 'São Paulo', 'SP', 'Brasil', '05001200', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Teatro Municipal', 'Teatro Municipal do Rio de Janeiro', '44445555000122', 'Teatro clássico e histórico focado em óperas e balés.', 'Praça Floriano', 'S/N', 'Centro', 'Rio de Janeiro', 'RJ', 'Brasil', '20031050', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Pedreira Paulo Leminski', 'Parque das Pedreiras Eventos', '11110000000177', 'O maior palco ao ar livre da América Latina.', 'Rua João Gava', '970', 'Abranches', 'Curitiba', 'PR', 'Brasil', '82130010', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Mineirão', 'Minas Arena Gestão de Instalações Esportivas', '66667777000144', 'Estádio histórico de Belo Horizonte ideal para festivais.', 'Avenida Antônio Abrahão Caram', '1001', 'São José', 'Belo Horizonte', 'MG', 'Brasil', '31275000', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Fundição Progresso', 'Centro Cultural Fundição Progresso', '88889999000133', 'Polo de shows e eventos independentes e culturais.', 'Rua dos Arcos', '24', 'Lapa', 'Rio de Janeiro', 'RJ', 'Brasil', '20230060', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Audio Club', 'Audio Club Eventos Culturais', '55556666000166', 'Casa de eventos premium com acústica impecável.', 'Avenida Francisco Matarazzo', '694', 'Água Branca', 'São Paulo', 'SP', 'Brasil', '05001100', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Teatro Positivo', 'UP Múltipla Gestão de Espaços', '22223333000155', 'Um dos maiores teatros corporativos e culturais do Brasil.', 'Rua Prof. Pedro Viriato Parigot de Souza', '5300', 'Campo Comprido', 'Curitiba', 'PR', 'Brasil', '81280330', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, approval_date, register_date, last_update_date, status_id, organizer_id)
VALUES
('Centro de Convenções', 'Centro de Convenções de Pernambuco', '77778888000199', 'Maior pavilhão de feiras e shows do Nordeste.', 'Avenida Professor Andrade Bezerra', 'S/N', 'Salgadinho', 'Olinda', 'PE', 'Brasil', '53110680', NOW(), NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

INSERT INTO tb_venues (name, legal_name, cnpj, description, street_address, street_address_number, neighborhood, city, state, country, zip_code, register_date, last_update_date, status_id, organizer_id)
VALUES
('Vivo Rio', 'Casa de Shows Aterro Ltda', '10102020000144', 'Moderna e renomada casa de espetáculos anexa ao MAM.', 'Avenida Infante Dom Henrique', '85', 'Parque do Flamengo', 'Rio de Janeiro', 'RJ', 'Brasil', '20021140', NOW(), NOW(), 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

-- Populando Venue Sectors (Setores dos Espaços)

-- Espaço das Américas (ID 1)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Pista Premium', 'Área VIP próxima ao palco', 2000, NOW(), NOW(), 1),
       ('Pista Comum', 'Área geral do evento', 5000, NOW(), NOW(), 1),
       ('Mezanino', 'Área elevada com vista privilegiada', 1000, NOW(), NOW(), 1);

-- Allianz Parque (ID 2)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Pista', 'Gramado do estádio', 15000, NOW(), NOW(), 2),
       ('Cadeira Inferior', 'Assentos no nível inferior', 10000, NOW(), NOW(), 2),
       ('Cadeira Superior', 'Assentos no nível superior', 12000, NOW(), NOW(), 2);

-- Teatro Municipal (ID 3)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Plateia', 'Assentos frontais ao palco', 800, NOW(), NOW(), 3),
       ('Frisas', 'Camarotes laterais inferiores', 200, NOW(), NOW(), 3),
       ('Balcão Nobre', 'Assentos em nível elevado', 500, NOW(), NOW(), 3);

-- Pedreira Paulo Leminski (ID 4)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Pista Única', 'Espaço amplo ao ar livre', 25000, NOW(), NOW(), 4),
       ('Área VIP', 'Espaço reservado nas laterais', 3000, NOW(), NOW(), 4);

-- Mineirão (ID 5)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Pista Premium', 'Gramado próximo ao palco', 10000, NOW(), NOW(), 5),
       ('Cadeira Especial', 'Assentos laterais', 20000, NOW(), NOW(), 5);

-- Fundição Progresso (ID 6)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Pista', 'Espaço principal', 3000, NOW(), NOW(), 6),
       ('Frisas', 'Área de camarotes', 500, NOW(), NOW(), 6);

-- Audio Club (ID 7)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Pista', 'Área em frente ao palco', 2500, NOW(), NOW(), 7),
       ('Mezanino', 'Área VIP superior', 700, NOW(), NOW(), 7);

-- Teatro Positivo (ID 8)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Plateia Inferior', 'Nível principal do teatro', 1400, NOW(), NOW(), 8),
       ('Plateia Superior', 'Nível elevado do teatro', 1000, NOW(), NOW(), 8);

-- Centro de Convenções (ID 9)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Pavilhão A', 'Espaço para grandes feiras', 5000, NOW(), NOW(), 9),
       ('Auditório Central', 'Espaço para palestras', 1500, NOW(), NOW(), 9);

-- Vivo Rio (ID 10)
INSERT INTO tb_venue_sectors (name, description, max_capacity, register_date, last_update_date, venue_id)
VALUES ('Setor 01', 'Cadeiras numeradas próximas ao palco', 1200, NOW(), NOW(), 10),
       ('Frisas', 'Assentos laterais', 300, NOW(), NOW(), 10),
       ('Camarote A', 'Área exclusiva superior', 500, NOW(), NOW(), 10);

-- Populando Event Categories
INSERT INTO tb_event_categories (category_id, name) VALUES (1, 'Música') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (2, 'Tecnologia') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (3, 'Gastronomia') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (4, 'Artes e Cultura') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (5, 'Cursos e Workshops') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (6, 'Esportes e Bem-estar') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (7, 'Comédia e Stand-up') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (8, 'Feiras e Negócios') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (9, 'Festas e Shows') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name) VALUES (10, 'Games e E-Sports') ON CONFLICT DO NOTHING;

-- Populando Events com referência aos Venues e Categories
-- Adicionado a coluna 'category_id'
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Rock Fest 2025', 'O maior festival de rock do ano.', 18, '2025-12-01 18:00:00', '2025-12-02 02:00:00', NOW(), NOW(), NOW(), 1, 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 2, 1); -- Categoria 1: Música

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Spring Boot Conf', 'Conferência avançada de Spring Boot.', 16, '2025-11-20 09:00:00', '2025-11-20 18:00:00', NOW(), NOW(), NOW(), 2, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 7); -- Categoria 2: Tecnologia

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Festival Gastronômico', 'Sabores de todo o mundo.', 0, '2025-11-25 12:00:00', '2025-11-25 22:00:00', NOW(), NOW(), NOW(), 3, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 9); -- Categoria 3: Gastronomia

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Maratona Tech', 'Hackathon 48h para desenvolvedores.', 18, '2025-12-05 19:00:00', '2025-12-07 19:00:00', NOW(), NOW(), NOW(), 2, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 2); -- Categoria 2: Tecnologia

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Concerto de Jazz', 'Noite de Jazz e Blues ao vivo.', 14, '2025-11-28 20:00:00', '2025-11-28 23:00:00', NOW(), NOW(), NOW(), 1, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 3); -- Categoria 1: Música

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Expo Arte Digital', 'Exposição de arte e novas mídias.', 0, '2025-12-10 10:00:00', '2025-12-15 20:00:00', NOW(), NOW(), NOW(), 4, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 6); -- Categoria 4: Artes e Cultura

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Samba de Raiz', 'Roda de samba tradicional.', 18, '2025-11-22 16:00:00', '2025-11-22 21:00:00', NOW(), NOW(), NOW(), 1, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 6); -- Categoria 1: Música

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Feira de Livros', 'Grande feira de livros com autores convidados.', 0, '2026-01-15 09:00:00', '2026-01-18 18:00:00', NOW(), NOW(), NOW(), 4, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 8); -- Categoria 4: Artes e Cultura

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Show de Comédia Stand-up', 'Os melhores comediantes da atualidade.', 16, '2025-11-29 21:00:00', '2025-11-29 23:00:00', NOW(), NOW(), NOW(), 7, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 10); -- Categoria 7: Comédia e Stand-up

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Palestra IA', 'O futuro da Inteligência Artificial.', 12, '2025-12-03 19:00:00', '2025-12-03 22:00:00', NOW(), NOW(), NOW(), 2, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 7); -- Categoria 2: Tecnologia

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Aula de Yoga no Parque', 'Sessão de yoga ao ar livre.', 0, '2025-11-23 08:00:00', '2025-11-23 09:30:00', NOW(), NOW(), NOW(), 6, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 4); -- Categoria 6: Esportes e Bem-estar

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Circuito de Corrida', 'Corrida de rua 10km.', 16, '2025-12-14 07:00:00', '2025-12-14 10:00:00', NOW(), NOW(), NOW(), 6, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 5); -- Categoria 6: Esportes e Bem-estar

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Festival de Cinema Indie', 'Mostra de filmes independentes.', 14, '2026-01-20 14:00:00', '2026-01-25 23:00:00', NOW(), NOW(), NOW(), 4, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 10); -- Categoria 4: Artes e Cultura

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Torneio de E-Sports', 'Competição de LoL e CS.', 16, '2025-12-20 10:00:00', '2025-12-21 22:00:00', NOW(), NOW(), NOW(), 10, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 2); -- Categoria 10: Games e E-Sports

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Bazar de Natal', 'Artesanato e comidas típicas de natal.', 0, '2025-12-12 11:00:00', '2025-12-13 19:00:00', NOW(), NOW(), NOW(), 8, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 1); -- Categoria 8: Feiras e Negócios

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Workshop de Fotografia', 'Aprenda a fotografar com mestres.', 18, '2026-02-05 14:00:00', '2026-02-05 18:00:00', NOW(), NOW(), NOW(), 5, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 3); -- Categoria 5: Cursos e Workshops

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Festa Eletrônica', 'DJs internacionais.', 18, '2025-12-27 22:00:00', '2025-12-28 06:00:00', NOW(), NOW(), NOW(), 9, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 1); -- Categoria 9: Festas e Shows

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Peça de Teatro: O Fantasma', 'Clássico do teatro mundial.', 12, '2026-03-10 20:00:00', '2026-03-10 23:00:00', NOW(), NOW(), NOW(), 4, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 10); -- Categoria 4: Artes e Cultura

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Show Acústico', 'Voz e violão com artistas locais.', 12, '2025-11-30 19:00:00', '2025-11-30 21:00:00', NOW(), NOW(), NOW(), 1, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 6); -- Categoria 1: Música

INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Palestra sobre Investimentos', 'Como investir em 2026.', 18, '2026-01-10 19:30:00', '2026-01-10 21:30:00', NOW(), NOW(), NOW(), 5, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 8); -- Categoria 5: Cursos e Workshops
-- Populando Event Sectors (Setores dos Eventos)
-- Lógica: name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id

-- Evento 1: Rock Fest 2025 (Local: Espaço das Américas - ID 1)
-- Venues Sectors: 1 (Pista Premium), 2 (Pista Comum), 3 (Mezanino)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Pista Premium Rock', 'Área VIP colada no palco com open bar.', NOW(), NOW(), FALSE, 1, 1),
       ('Pista Geral Rock', 'Área ampla para curtir o festival.', NOW(), NOW(), FALSE, 1, 2),
       ('Mezanino VIP Rock', 'Visão elevada com assentos livres.', NOW(), NOW(), FALSE, 1, 3);

-- Evento 2: Spring Boot Conf (Local: Audio Club - ID 7)
-- Venues Sectors: 16 (Pista), 17 (Mezanino)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Plenária Principal', 'Área adaptada com cadeiras para as palestras.', NOW(), NOW(), TRUE, 2, 16),
       ('Área Networking', 'Mezanino reservado para palestrantes e VIPs.', NOW(), NOW(), FALSE, 2, 17);

-- Evento 3: Festival Gastronômico (Local: Centro de Convenções - ID 9)
-- Venues Sectors: 20 (Pavilhão A)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Área de Degustação', 'Espaço amplo com stands de restaurantes.', NOW(), NOW(), FALSE, 3, 20);

-- Evento 4: Maratona Tech (Local: Allianz Parque - ID 2)
-- Venues Sectors: 4 (Pista)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Arena Hackathon', 'Gramado coberto com mesas de desenvolvimento.', NOW(), NOW(), FALSE, 4, 4);

-- Evento 5: Concerto de Jazz (Local: Teatro Municipal - ID 3)
-- Venues Sectors: 7 (Plateia), 8 (Frisas)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Plateia Jazz', 'Assentos frontais para apreciar a acústica.', NOW(), NOW(), TRUE, 5, 7),
       ('Camarote Blue Note', 'Assentos exclusivos laterais.', NOW(), NOW(), TRUE, 5, 8);

-- Evento 6: Expo Arte Digital (Local: Fundição Progresso - ID 6)
-- Venues Sectors: 14 (Pista)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Galeria Imersiva', 'Pista livre para caminhar entre as projeções.', NOW(), NOW(), FALSE, 6, 14);

-- Evento 7: Samba de Raiz (Local: Fundição Progresso - ID 6)
-- Venues Sectors: 14 (Pista), 15 (Frisas)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Roda de Samba', 'Pista principal ao redor dos músicos.', NOW(), NOW(), FALSE, 7, 14),
       ('Camarote do Samba', 'Área elevada e reservada para grupos.', NOW(), NOW(), FALSE, 7, 15);

-- Evento 8: Feira de Livros (Local: Teatro Positivo - ID 8)
-- Venues Sectors: 18 (Plateia Inferior)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Palco de Autores', 'Apresentações e sessões de autógrafos.', NOW(), NOW(), TRUE, 8, 18);

-- Evento 9: Show de Comédia Stand-up (Local: Vivo Rio - ID 10)
-- Venues Sectors: 22 (Setor 01), 23 (Frisas)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Setor Risada VIP', 'Assentos colados no palco.', NOW(), NOW(), TRUE, 9, 22),
       ('Frisas Comedy', 'Visão lateral com serviço de bar.', NOW(), NOW(), TRUE, 9, 23);

-- Evento 10: Palestra IA (Local: Audio Club - ID 7)
-- Venues Sectors: 16 (Pista)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Auditório IA', 'Área principal adaptada com poltronas.', NOW(), NOW(), TRUE, 10, 16);

-- Evento 11: Aula de Yoga no Parque (Local: Pedreira Paulo Leminski - ID 4)
-- Venues Sectors: 10 (Pista Única)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Tapetes de Yoga', 'Amplo espaço ao ar livre para a prática.', NOW(), NOW(), FALSE, 11, 10);

-- Evento 12: Circuito de Corrida (Local: Mineirão - ID 5)
-- Venues Sectors: 12 (Pista Premium)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Arena do Corredor', 'Área de largada, chegada e hidratação.', NOW(), NOW(), FALSE, 12, 12);

-- Evento 13: Festival de Cinema Indie (Local: Vivo Rio - ID 10)
-- Venues Sectors: 22 (Setor 01)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Plateia Cinema', 'Cadeiras confortáveis e numeradas para exibição.', NOW(), NOW(), TRUE, 13, 22);

-- Evento 14: Torneio de E-Sports (Local: Allianz Parque - ID 2)
-- Venues Sectors: 5 (Cadeira Inferior), 6 (Cadeira Superior)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Cadeira Gamer Prata', 'Excelente visão do palco e telões.', NOW(), NOW(), TRUE, 14, 5),
       ('Cadeira Gamer Bronze', 'Visão panorâmica de toda a arena.', NOW(), NOW(), TRUE, 14, 6);

-- Evento 15: Bazar de Natal (Local: Espaço das Américas - ID 1)
-- Venues Sectors: 2 (Pista Comum)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Vila Natalina', 'Circulação livre por todos os stands.', NOW(), NOW(), FALSE, 15, 2);

-- Evento 16: Workshop de Fotografia (Local: Teatro Municipal - ID 3)
-- Venues Sectors: 7 (Plateia)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Área de Instrução', 'Assentos numerados para melhor visualização.', NOW(), NOW(), TRUE, 16, 7);

-- Evento 17: Festa Eletrônica (Local: Espaço das Américas - ID 1)
-- Venues Sectors: 1 (Pista Premium), 2 (Pista Comum)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Front Stage DJ', 'Acesso privilegiado e colado no DJ.', NOW(), NOW(), FALSE, 17, 1),
       ('Pista Eletrônica', 'Pista principal de dança.', NOW(), NOW(), FALSE, 17, 2);

-- Evento 18: Peça de Teatro: O Fantasma (Local: Vivo Rio - ID 10)
-- Venues Sectors: 22 (Setor 01), 24 (Camarote A)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Plateia VIP Teatro', 'Visão frontal ininterrupta.', NOW(), NOW(), TRUE, 18, 22),
       ('Camarote Exclusivo', 'Conforto e atendimento especial.', NOW(), NOW(), TRUE, 18, 24);

-- Evento 19: Show Acústico (Local: Fundição Progresso - ID 6)
-- Venues Sectors: 14 (Pista)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Pista Banquinho e Violão', 'Clima intimista e descontraído.', NOW(), NOW(), FALSE, 19, 14);

-- Evento 20: Palestra sobre Investimentos (Local: Teatro Positivo - ID 8)
-- Venues Sectors: 18 (Plateia Inferior), 19 (Plateia Superior)
INSERT INTO tb_event_sectors (name, description, register_date, last_update_date, has_numbered_seats, event_id, venue_sector_id)
VALUES ('Plenária Investidores', 'Cadeiras frontais com mesa de apoio.', NOW(), NOW(), TRUE, 20, 18),
       ('Galeria de Estudantes', 'Visão ampla do evento.', NOW(), NOW(), TRUE, 20, 19);

-- Populando Ticket Batches (Lotes de Ingressos)
-- Lógica: batch_number, price, total_tickets, sold_tickets, event_sector_id

-- Evento 1: Rock Fest 2025 (Festival com alta demanda -> 3 lotes)
-- ES 1 (Pista Premium Rock, Máx: 2000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 300.00, 500, 0, 1),
(2, 350.00, 1000, 0, 1),
(3, 400.00, 500, 0, 1);
-- ES 2 (Pista Geral Rock, Máx: 5000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 150.00, 1500, 0, 2),
(2, 180.00, 2000, 0, 2),
(3, 200.00, 1500, 0, 2);
-- ES 3 (Mezanino VIP Rock, Máx: 1000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 250.00, 400, 0, 3),
(2, 300.00, 600, 0, 3);

-- Evento 2: Spring Boot Conf (Conferência -> Lote Early Bird e Lote Geral)
-- ES 4 (Plenária Principal, Máx: 2500)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 100.00, 500, 0, 4),
(2, 150.00, 2000, 0, 4);
-- ES 5 (Área Networking, Máx: 700)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 250.00, 200, 0, 5),
(2, 300.00, 500, 0, 5);

-- Evento 3: Festival Gastronômico (Feira -> Lote Único, menos ingressos que a capacidade total)
-- ES 6 (Área de Degustação, Máx: 5000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 50.00, 4000, 0, 6);

-- Evento 4: Maratona Tech (Hackathon -> Lote Único de Inscrição)
-- ES 7 (Arena Hackathon, Máx: 15000 - Carga liberada: 1000 para conforto)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 30.00, 1000, 0, 7);

-- Evento 5: Concerto de Jazz (Teatro Clássico -> Lote Único)
-- ES 8 (Plateia Jazz, Máx: 800) e ES 9 (Camarote Blue Note, Máx: 200)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 120.00, 800, 0, 8),
(1, 200.00, 200, 0, 9);

-- Evento 6: Expo Arte Digital (Exposição contínua -> Lote Único)
-- ES 10 (Galeria Imersiva, Máx: 3000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 40.00, 3000, 0, 10);

-- Evento 7: Samba de Raiz (Show Popular -> 2 Lotes Pista, 1 Lote Camarote)
-- ES 11 (Roda de Samba, Máx: 3000) e ES 12 (Camarote do Samba, Máx: 500)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 30.00, 1000, 0, 11),
(2, 50.00, 2000, 0, 11),
(1, 100.00, 500, 0, 12);

-- Evento 8: Feira de Livros (Evento aberto com ingresso barato -> Lote Único)
-- ES 13 (Palco de Autores, Máx: 1400)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 10.00, 1400, 0, 13);

-- Evento 9: Show de Comédia Stand-up (Lote Único)
-- ES 14 (Setor Risada VIP, Máx: 1200) e ES 15 (Frisas Comedy, Máx: 300)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 80.00, 1200, 0, 14),
(1, 120.00, 300, 0, 15);

-- Evento 10: Palestra IA (2 Lotes)
-- ES 16 (Auditório IA, Máx: 2500 - Carga disponibilizada de 2000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 60.00, 1000, 0, 16),
(2, 90.00, 1000, 0, 16);

-- Evento 11: Aula de Yoga no Parque (Lote Único limitado a 500 pessoas no gramado)
-- ES 17 (Tapetes de Yoga, Máx: 25000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 20.00, 500, 0, 17);

-- Evento 12: Circuito de Corrida (Vários lotes de inscrição)
-- ES 18 (Arena do Corredor, Máx: 10000 - Carga: 6000 corredores)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 80.00, 2000, 0, 18),
(2, 100.00, 3000, 0, 18),
(3, 120.00, 1000, 0, 18);

-- Evento 13: Festival de Cinema Indie (Lote Único)
-- ES 19 (Plateia Cinema, Máx: 1200 - Limitado a 800 para exibição focada)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 25.00, 800, 0, 19);

-- Evento 14: Torneio de E-Sports (Alta demanda jovem -> 2 lotes)
-- ES 20 (Cadeira Gamer Prata, Máx: 10000) e ES 21 (Cadeira Gamer Bronze, Máx: 12000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 90.00, 4000, 0, 20),
(2, 120.00, 6000, 0, 20),
(1, 60.00, 5000, 0, 21),
(2, 80.00, 7000, 0, 21);

-- Evento 15: Bazar de Natal (Entrada Simbólica -> Lote Único)
-- ES 22 (Vila Natalina, Máx: 5000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 5.00, 5000, 0, 22);

-- Evento 16: Workshop de Fotografia (Limitado -> Lote Único para 200 alunos)
-- ES 23 (Área de Instrução, Máx: 800)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 150.00, 200, 0, 23);

-- Evento 17: Festa Eletrônica (Múltiplos Lotes)
-- ES 24 (Front Stage DJ, Máx: 2000) e ES 25 (Pista Eletrônica, Máx: 5000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 200.00, 1000, 0, 24),
(2, 250.00, 1000, 0, 24),
(1, 100.00, 2000, 0, 25),
(2, 140.00, 3000, 0, 25);

-- Evento 18: Peça de Teatro (Lote Único - Setores Específicos)
-- ES 26 (Plateia VIP Teatro, Máx: 1200) e ES 27 (Camarote Exclusivo, Máx: 500)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 70.00, 1200, 0, 26),
(1, 150.00, 500, 0, 27);

-- Evento 19: Show Acústico (Lote Único - Não esgota capacidade)
-- ES 28 (Pista Banquinho e Violão, Máx: 3000 - Carga: 2000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 60.00, 2000, 0, 28);

-- Evento 20: Palestra sobre Investimentos (Demanda Crescente)
-- ES 29 (Plenária Investidores, Máx: 1400) e ES 30 (Galeria de Estudantes, Máx: 1000)
INSERT INTO tb_ticket_batches (batch_number, price, total_tickets, sold_tickets, event_sector_id) VALUES
(1, 100.00, 700, 0, 29),
(2, 150.00, 700, 0, 29),
(1, 70.00, 1000, 0, 30);

CREATE MATERIALIZED VIEW IF NOT EXISTS vw_event_min_details AS
SELECT
    e.event_id,
    e.title,
    e.start_date,
	ec.name AS category_name,
    v.name  AS venue_name,
    v.city  AS venue_city,
    v.state AS venue_state,
    prices.starting_price,
    img.image_keys
FROM tb_events e
INNER JOIN tb_event_categories ec ON ec.category_id = e.category_id
INNER JOIN tb_venues v ON v.venue_id = e.venue_id
INNER JOIN (
    SELECT es.event_id, MIN(tb.price) AS starting_price
    FROM tb_event_sectors es
    INNER JOIN tb_ticket_batches tb ON tb.event_sector_id = es.sector_id
    GROUP BY es.event_id
) prices ON prices.event_id = e.event_id
LEFT JOIN (
    SELECT event_id, ARRAY_AGG(s3_key ORDER BY is_main_image DESC) AS image_keys
    FROM tb_event_images
    GROUP BY event_id
) img ON img.event_id = e.event_id
WHERE e.status_id = 2
AND e.visibility_id = 1;

CREATE INDEX IF NOT EXISTS idx_vw_event_min_details_date
    ON vw_event_min_details(start_date);

-- para a subquery de preço
CREATE INDEX IF NOT EXISTS idx_ticket_batches_sector_price
    ON tb_ticket_batches(event_sector_id, price);

-- para a subquery de imagens
CREATE INDEX IF NOT EXISTS idx_event_images_event_main
    ON tb_event_images(event_id, is_main_image DESC);

-- para o filtro principal
CREATE INDEX IF NOT EXISTS idx_events_status_visibility
    ON tb_events(status_id, visibility_id);