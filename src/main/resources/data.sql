INSERT INTO tb_roles (role_id, name) VALUES (1, 'admin') ON CONFLICT DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (2, 'organizer') ON CONFLICT DO NOTHING;
INSERT INTO tb_roles (role_id, name) VALUES (3, 'client') ON CONFLICT DO NOTHING;