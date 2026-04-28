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
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (1, 'Música', 'musica') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (2, 'Tecnologia', 'tecnologia') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (3, 'Gastronomia', 'gastronomia') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (4, 'Artes e Cultura', 'artes-e-cultura') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (5, 'Cursos e Workshops', 'cursos-e-workshops') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (6, 'Esportes e Bem-estar', 'esportes-e-bem-estar') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (7, 'Comédia e Stand-up', 'comedia-e-stand-up') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (8, 'Feiras e Negócios', 'feiras-e-negocios') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (9, 'Festas e Shows', 'festas-e-shows') ON CONFLICT DO NOTHING;
INSERT INTO tb_event_categories (category_id, name, slug) VALUES (10, 'Games e E-Sports', 'games-e-esports') ON CONFLICT DO NOTHING;

-- Populando Events com referência aos Venues e Categories
-- Adicionado a coluna 'category_id'
-- Populando Events com descrições detalhadas (Long-form)

-- 1. Rock Fest 2025
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Rock Fest 2025', 'O Rock Fest 2025 não é apenas um evento musical, mas uma celebração da história e da energia bruta do gênero que definiu gerações. Prepare-se para uma experiência sensorial completa, onde o som das guitarras distorcidas ecoará por cada centímetro do local, criando uma atmosfera de pura eletricidade e camaradagem. Este ano, o festival traz um lineup cuidadosamente selecionado, unindo lendas vivas do rock clássico a novas bandas que estão moldando o futuro do som alternativo. Além das apresentações nos palcos principais, o evento contará com áreas de convivência temáticas, incluindo uma vila de vinis para colecionadores, exposições de fotos raras de turnês históricas e uma praça de alimentação com foco em culinária urbana de alta qualidade. A estrutura foi planejada para oferecer o máximo conforto, com áreas de descanso sombreadas e pontos de hidratação gratuitos. A segurança será prioridade, garantindo que todos os fãs possam aproveitar cada solo de guitarra com tranquilidade. O evento também se orgulha de suas iniciativas sustentáveis, com um sistema rigoroso de gestão de resíduos e incentivo ao uso de transporte coletivo. Imagine-se sob as luzes do palco, sentindo a vibração do baixo no peito enquanto milhares de vozes se unem em um coro épico. O Rock Fest 2025 promete ser o marco cultural do ano, proporcionando memórias que durarão a vida toda. Desde o momento em que os portões se abrem até o último acorde da noite, você será transportado para um universo onde o rock é a única lei. Não perca a chance de presenciar a história sendo escrita e de se conectar com uma comunidade que compartilha a mesma paixão visceral pela música. Garanta seu lugar na primeira fila e prepare-se para uma jornada de volume alto e emoção intensa. Este é o festival que você esperou o ano inteiro, e a espera finalmente acabou. O palco está pronto, os amplificadores estão ligados e a única coisa que falta é você. Venha fazer parte do rugido da multidão e sinta o poder transformador do verdadeiro rock and roll em uma noite que desafiará todos os seus sentidos.', 18, '2025-12-01 18:00:00', '2025-12-02 02:00:00', NOW(), NOW(), NOW(), 1, 1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 2, 1);

-- 2. Spring Boot Conf
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Spring Boot Conf', 'A Spring Boot Conf é o epicentro da inovação para desenvolvedores Java que buscam elevar suas habilidades ao próximo nível e dominar as tecnologias que sustentam as maiores arquiteturas de software do mundo. Neste evento intensivo, mergulharemos profundamente nas entranhas do ecossistema Spring, explorando desde as novidades do Spring Framework 6 e Spring Boot 3 até técnicas avançadas de microserviços, segurança com Spring Security e otimização de performance em ambientes cloud-native. O cronograma foi estruturado para equilibrar teoria e prática, apresentando palestras de arquitetos renomados e sessões de "live coding" onde você poderá ver, em tempo real, a resolução de problemas complexos de escalabilidade. Além do conteúdo técnico de ponta, a conferência oferece um ambiente de networking incomparável, conectando desenvolvedores juniores, seniores e líderes de tecnologia de diversas indústrias para trocar experiências sobre desafios reais de produção. Discutiremos a adoção de imagens nativas com GraalVM, o uso de Spring Data para diferentes paradigmas de persistência e como implementar observabilidade de ponta a ponta com as ferramentas mais modernas do mercado. A Spring Boot Conf é mais do que um aprendizado técnico; é uma imersão na cultura de excelência em engenharia de software. Os participantes terão acesso exclusivo a workshops práticos no período da tarde, onde poderão testar novas funcionalidades em ambientes controlados sob a supervisão de especialistas. Se você deseja construir aplicações resilientes, escaláveis e de fácil manutenção, este evento é o seu ponto de partida. Prepare seu notebook, traga suas dúvidas mais cabeludas e esteja pronto para transformar sua maneira de codificar. O futuro do backend está sendo construído agora, e a Spring Boot Conf é a sua porta de entrada para ser um protagonista nessa jornada tecnológica. Junte-se a nós para um dia inteiro de aprendizado contínuo, inspiração técnica e conexões que impulsionarão sua carreira para o topo do mercado de desenvolvimento global.', 16, '2025-11-20 09:00:00', '2025-11-20 18:00:00', NOW(), NOW(), NOW(), 2, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 7);

-- 3. Festival Gastronômico
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Festival Gastronômico', 'Prepare seu paladar para uma volta ao mundo sem precisar de passaporte. O Festival Gastronômico: Sabores de Todo o Mundo é uma celebração exuberante da diversidade culinária, reunindo chefs renomados, produtores locais e entusiastas da boa mesa em um único e vibrante espaço. Durante todo o evento, os visitantes terão a oportunidade de degustar pratos autênticos de cinco continentes, desde as especiarias aromáticas da Tailândia até os clássicos refinados da pâtisserie francesa, passando pelos assados tradicionais da Argentina e a riqueza de temperos da culinária brasileira regional. Cada barraca foi cuidadosamente selecionada para oferecer uma experiência de alta fidelidade aos sabores originais, utilizando ingredientes frescos e técnicas tradicionais. Além das estações de comida, o festival conta com uma programação rica de aulas-show, onde chefs estrelados compartilham segredos de cozinha, técnicas de empratamento e harmonização com vinhos e cervejas artesanais. Para os amantes de bebidas, teremos uma curadoria especial de mixologia com coquetéis exclusivos criados para o evento. O ambiente é projetado para ser acolhedor para todas as idades, com música ao vivo suave, áreas de piquenique e um espaço dedicado às crianças, onde elas podem aprender sobre a origem dos alimentos de forma lúdica. O Festival Gastronômico também assume um compromisso com a sustentabilidade, utilizando utensílios biodegradáveis e promovendo o desperdício zero através de parcerias com bancos de alimentos locais. É a oportunidade perfeita para reunir amigos e familiares, explorar novos sabores e descobrir aquela receita que se tornará sua favorita. Venha sentir o aroma das cozinhas do mundo, ouvir as histórias por trás de cada ingrediente e celebrar a cultura através do paladar. Este é mais do que um evento de comida; é um banquete para a alma e um tributo à criatividade humana manifestada na gastronomia. Não perca a chance de viver essa experiência inesquecível e deliciosa, onde cada mordida é uma nova descoberta.', 0, '2025-11-25 12:00:00', '2025-11-25 22:00:00', NOW(), NOW(), NOW(), 3, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 9);

-- 4. Maratona Tech
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Maratona Tech', 'A Maratona Tech é o desafio definitivo para mentes inquietas, desenvolvedores audaciosos e designers visionários que acreditam que o código pode mudar o mundo. Durante 48 horas ininterruptas, as equipes serão imersas em um ambiente de altíssima pressão e criatividade ilimitada para solucionar problemas reais propostos por grandes players da indústria tecnológica. Este hackathon não é apenas uma competição, mas um laboratório de inovação acelerado onde ideias abstratas se transformam em protótipos funcionais. Os participantes terão acesso a mentorias exclusivas com especialistas em UX/UI, arquitetura de sistemas, business design e pitch de vendas, garantindo que o projeto final tenha viabilidade técnica e comercial. A estrutura do evento foi pensada para o máximo desempenho: zonas de descanso (nap zones), alimentação energética disponível 24 horas por dia e infraestrutura de rede de ultravelocidade. Além da competição técnica, a Maratona Tech promove uma troca de conhecimentos intensa, onde a colaboração supera a rivalidade. No final das 48 horas, as equipes apresentarão seus projetos para um júri composto por investidores de risco e diretores de tecnologia, com premiações que incluem desde equipamentos de última geração até bolsas de aceleração para as startups nascentes. Se você é um entusiasta de inteligência artificial, blockchain, IoT ou mobile, encontrará aqui o terreno fértil para testar seus limites e expandir seu portfólio. É o momento de tirar aquele projeto da gaveta, formar um esquadrão de elite e mostrar ao mercado do que você é capaz. Prepare o café, ajuste seu editor de código e venha fazer parte de uma comunidade que não dorme enquanto o problema não é resolvido. A Maratona Tech é onde o futuro começa a ser escrito, linha por linha de código. Você está pronto para o desafio? A contagem regressiva começou e a glória tecnológica espera por aqueles que têm coragem de inovar sob pressão.', 18, '2025-12-05 19:00:00', '2025-12-07 19:00:00', NOW(), NOW(), NOW(), 2, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 2);

-- 5. Concerto de Jazz
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Concerto de Jazz', 'Deixe-se envolver pela sofisticação e pelo improviso sublime em nossa Noite de Jazz e Blues ao vivo, um evento dedicado a celebrar a alma e a história desses gêneros fundamentais. O palco será transformado em um refúgio de elegância acústica, onde músicos virtuosos de renome nacional e internacional se reunirão para criar uma tapeçaria sonora inesquecível. O repertório da noite viajará pelos clássicos de New Orleans, as nuances do Bebop, a suavidade do Cool Jazz e a melancolia visceral do Blues do Delta. Cada nota tocada é um diálogo entre os instrumentos, onde o saxofone chora, o piano brilha e a seção rítmica mantém o pulso constante de uma noite mágica. O ambiente foi planejado para oferecer uma experiência íntima e imersiva, com iluminação baixa, acústica impecável e uma disposição de assentos que garante que cada espectador se sinta parte do espetáculo. Além da música, os presentes poderão desfrutar de uma carta de drinks clássicos e petiscos gourmet especialmente selecionados para harmonizar com a atmosfera noir da noite. Este concerto é o destino ideal para quem busca uma pausa no caos urbano e deseja se reconectar com a arte em sua forma mais pura e espontânea. É um convite à contemplação, onde o tempo parece parar e apenas a melodia importa. Durante os intervalos, haverá breves exposições sobre a evolução do jazz e sua influência na cultura popular moderna, proporcionando uma camada educacional ao entretenimento. Traga seus amigos ou venha sozinho para se perder nos acordes complexos e nas vozes potentes que ecoarão pelo salão. O Jazz e o Blues não são apenas estilos musicais; são sentimentos traduzidos em som, e nesta noite, esses sentimentos ganharão vida de forma vibrante e autêntica. Reserve seu lugar e prepare-se para ser transportado por uma jornada musical que tocará seu coração e elevará seu espírito. Sinta a batida, feche os olhos e deixe o jazz levar você.', 14, '2025-11-28 20:00:00', '2025-11-28 23:00:00', NOW(), NOW(), NOW(), 1, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 3);

-- 6. Expo Arte Digital
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Expo Arte Digital', 'Bem-vindo à Expo Arte Digital, uma jornada fascinante pela fronteira final da criatividade humana, onde os pincéis são substituídos por algoritmos e a tela é o próprio espaço infinito. Esta exposição reúne as obras mais inovadoras de artistas digitais contemporâneos que utilizam inteligência artificial, realidade aumentada, projeções mapeadas e arte generativa para questionar nossa percepção do real. Os visitantes serão convidados a interagir com instalações que respondem ao movimento, ao som e até ao toque, tornando cada visita uma experiência única e personalizada. Explore galerias repletas de NFTs icônicos, esculturas impressas em 3D de complexidade impossível e salas de imersão em realidade virtual que transportam você para universos paralelos de tirar o fôlego. A Expo Arte Digital não é apenas uma mostra visual; é um debate sobre o papel da tecnologia na evolução da estética e da expressão emocional. Além das obras expostas, o evento contará com palestras de curadores e workshops sobre ferramentas de criação digital, permitindo que o público entenda os processos por trás da arte feita por máquinas e humanos em colaboração. O objetivo é democratizar o acesso às novas mídias e inspirar uma nova geração de criadores a explorar o potencial ilimitado das ferramentas binárias. Em um mundo cada vez mais conectado, esta exposição serve como um espelho de nossa sociedade tecnocêntrica, refletindo nossas ansiedades, sonhos e esperanças através de pixels e códigos de luz. Seja você um colecionador de arte tradicional, um entusiasta de tecnologia ou apenas alguém curioso pelo novo, a Expo Arte Digital promete expandir seus horizontes e desafiar suas ideias sobre o que define uma obra de arte. Venha presenciar o nascimento de novos movimentos estéticos e se maravilhar com a beleza que emerge da interseção entre o silício e a alma humana. A entrada é livre para todas as idades, tornando-a o passeio ideal para famílias que desejam explorar o futuro da cultura juntas hoje.', 0, '2025-12-10 10:00:00', '2025-12-15 20:00:00', NOW(), NOW(), NOW(), 4, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 6);

-- 7. Samba de Raiz
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Samba de Raiz', 'Prepare o seu coração para a batida do surdo e o brilho do pandeiro no nosso Samba de Raiz, uma celebração autêntica da maior expressão cultural do povo brasileiro. Esta roda de samba tradicional foi concebida para honrar as origens do gênero, trazendo à tona as composições imortais de mestres como Cartola, Nelson Cavaquinho, Dona Ivone Lara e Adoniran Barbosa. Imagine-se em um ambiente acolhedor, com mesas dispostas ao redor dos músicos, criando aquela proximidade mágica que só o verdadeiro samba proporciona. Não há palcos distantes aqui; a música acontece no centro de tudo, em um diálogo constante entre os instrumentos de corda e a percussão refinada. O evento contará com a presença de veteranos das escolas de samba e novos talentos que mantêm a chama do samba clássico acesa. Para acompanhar a cantoria, o cardápio oferecerá o melhor da gastronomia de boteco: feijoada completa, caldinhos variados, pastéis crocantes e, claro, aquela cerveja gelada ou uma caipirinha preparada no capricho. O Samba de Raiz é um espaço de resistência e alegria, onde cada verso cantado em coro reafirma nossa identidade e nossa história. É o lugar perfeito para quem quer dançar, cantar a plenos pulmões ou simplesmente apreciar a poesia cotidiana das letras que falam de amor, cotidiano e esperança. A atmosfera é de total confraternização, onde estranhos se tornam amigos através do ritmo comum. O evento também contará com uma breve introdução sobre a história das comunidades que deram origem ao samba, enriquecendo a experiência com contexto cultural e social. Venha sentir o "axé" dessa roda, onde o improviso é bem-vindo e a alegria é obrigatória. Traga seu sapato de dança e sua disposição, pois a tarde promete se estender até o pôr do sol em um clima de pura harmonia e celebração. O samba não é apenas música, é um estilo de vida, e nesta tarde, você será o convidado de honra para celebrar essa herança maravilhosa conosco.', 18, '2025-11-22 16:00:00', '2025-11-22 21:00:00', NOW(), NOW(), NOW(), 1, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 6);

-- 8. Feira de Livros
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Feira de Livros', 'A Grande Feira de Livros de 2026 convida todos os apaixonados por literatura para um mergulho profundo no oceano das palavras e das ideias. Este evento monumental transformará o pavilhão em uma verdadeira biblioteca viva, abrigando milhares de títulos que abrangem desde os clássicos universais até as publicações independentes mais vanguardistas. Com a presença confirmada de autores best-sellers, poetas contemporâneos e ilustradores renomados, a feira oferece uma oportunidade rara de interação direta entre criadores e leitores. A programação está repleta de sessões de autógrafos, mesas redondas sobre o futuro da narrativa digital, oficinas de escrita criativa e contação de histórias para o público infantil. Cada corredor foi planejado para ser uma descoberta: teremos o "Beco dos Sebos" para quem busca raridades e edições esgotadas, a "Alameda dos Quadrinhos" para os fãs de nona arte e um espaço dedicado exclusivamente à literatura regional e autores locais. Além dos livros, a feira contará com exposições de arte inspiradas em obras literárias e apresentações musicais acústicas que criam um clima de serenidade ideal para a leitura. Para os profissionais do setor, haverá painéis sobre o mercado editorial, autopublicação e design de capas. Acreditamos que a leitura é uma ferramenta de transformação social, por isso, o evento contará com pontos de arrecadação de livros para bibliotecas comunitárias e descontos especiais para estudantes e professores. O ambiente é totalmente acessível, com áreas de descanso e cafés temáticos onde você pode começar a ler sua nova aquisição imediatamente. Se você busca conhecimento, escapismo ou simplesmente uma boa história para compartilhar, a Feira de Livros é o seu lugar. Venha celebrar o poder da literatura de abrir mentes, conectar culturas e atravessar gerações. Deixe-se perder entre as estantes e encontre aquele livro que mudará sua vida para sempre. Este é um convite para desacelerar, respirar o aroma de papel novo e se deixar levar pela imaginação. Esperamos você para escrevermos juntos mais um capítulo desta história maravilhosa.', 0, '2026-01-15 09:00:00', '2026-01-18 18:00:00', NOW(), NOW(), NOW(), 4, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 8);

-- 9. Show de Comédia Stand-up
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Show de Comédia Stand-up', 'Prepare-se para uma noite onde o riso é o protagonista absoluto e o mau humor não tem vez. O Show de Comédia Stand-up: Melhores Comediantes da Atualidade reúne um time de elite do humor nacional para uma maratona de observações sagazes, críticas sociais afiadas e histórias cotidianas transformadas em pura gargalhada. Esqueça os clichês e prepare-se para um estilo de comédia inteligente, onde o "timing" é preciso e a interação com a plateia cria momentos únicos e irrepetíveis. Nossos comediantes trazem estilos variados, desde o humor observacional que faz você se identificar com cada situação, até o humor mais ácido e surrealista que desafia os limites do convencional. O palco será o cenário de desabafos hilários sobre relacionamentos, trabalho, tecnologia e as pequenas tragédias da vida moderna que só o riso pode curar. O local do evento foi escolhido para proporcionar a melhor visibilidade e acústica, garantindo que você não perca nenhuma piada, nem mesmo aquele sussurro sarcástico no final de uma frase. Além do show principal, teremos um "open mic" surpresa para novos talentos mostrarem seu valor em poucos minutos. A noite é ideal para relaxar com amigos, celebrar um aniversário ou simplesmente ter aquela dose necessária de endorfina após uma semana cansativa. Teremos serviço de bar completo com drinks temáticos e petiscos que podem ser consumidos durante a apresentação. O riso é, comprovadamente, o melhor remédio, e esta noite será a sua receita completa para o bem-estar. Venha preparado para rir de si mesmo, do mundo e de tudo o que nos cerca. Lembre-se: o que acontece no show, fica no show (a menos que seja tão engraçado que você precise postar). Garanta seu ingresso com antecedência, pois as noites de stand-up costumam esgotar rapidamente. Não deixe para amanhã a gargalhada que você pode dar hoje. Venha fazer parte da plateia mais animada da cidade e saia com as bochechas doendo de tanto sorrir. O microfone está aberto e a diversão é garantida!', 16, '2025-11-29 21:00:00', '2025-11-29 23:00:00', NOW(), NOW(), NOW(), 7, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 10);

-- 10. Palestra IA
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Palestra IA', 'Estamos vivendo a maior revolução tecnológica da história da humanidade, e a Inteligência Artificial é o motor dessa transformação. A palestra "O Futuro da IA" é um evento imperdível para quem deseja compreender como os modelos de linguagem, a visão computacional e a automação inteligente estão redefinindo indústrias inteiras, do mercado financeiro à medicina de precisão. O palestrante, um dos principais pesquisadores da área, conduzirá o público por uma jornada que vai além do hype, explorando as implicações éticas, as oportunidades econômicas e os desafios sociológicos de um mundo onde máquinas podem aprender e criar. Discutiremos o impacto da IA generativa na criatividade humana, o futuro do trabalho em um cenário de automação em larga escala e como indivíduos e empresas podem se preparar para essa nova era. A apresentação será enriquecida com demonstrações práticas de ferramentas de IA de última geração, mostrando como elas podem ser utilizadas para aumentar a produtividade e resolver problemas complexos que antes pareciam insolúveis. Teremos também um painel de debate com especialistas em direito digital e ética, abordando a questão da privacidade de dados e a responsabilidade algorítmica. O evento é voltado tanto para profissionais da tecnologia quanto para curiosos, estudantes e líderes de negócios que precisam tomar decisões estratégicas baseadas nas tendências tecnológicas. O conhecimento é a única bússola confiável em tempos de mudanças aceleradas, e esta palestra fornecerá os insights necessários para você não apenas acompanhar, mas liderar no novo paradigma. Ao final, haverá uma sessão estendida de perguntas e respostas, permitindo que a plateia interaja diretamente com o palestrante. O futuro não é algo que simplesmente acontece; é algo que construímos. Venha entender as ferramentas que estão moldando esse amanhã e descubra como você pode se posicionar na vanguarda da revolução digital. Garanta seu lugar e prepare-se para ter sua visão de mundo expandida.', 12, '2025-12-03 19:00:00', '2025-12-03 22:00:00', NOW(), NOW(), NOW(), 2, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 7);

-- 11. Aula de Yoga no Parque
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Aula de Yoga no Parque', 'Encontre o seu equilíbrio e sintonize-se com a natureza nesta sessão revitalizante de Yoga ao ar livre. Sob a luz suave da manhã e rodeado pelo verde revigorante do parque, você será convidado a desacelerar e focar na única coisa que realmente importa: o momento presente. Esta aula foi desenhada para acolher praticantes de todos os níveis, desde iniciantes que nunca pisaram em um "mat" até iogues experientes que buscam aprofundar sua prática em um cenário inspirador. O instrutor guiará o grupo através de uma sequência fluida de asanas (posturas) que visam fortalecer o corpo, aumentar a flexibilidade e liberar as tensões acumuladas durante a semana. O foco especial na respiração consciente (pranayama) ajudará a acalmar a mente e reduzir os níveis de estresse, proporcionando uma sensação de clareza mental e paz interior. Após a prática física, teremos um momento dedicado à meditação guiada, aproveitando os sons ambientes — o canto dos pássaros e o balanço das árvores — para facilitar a introspecção. Participar de uma aula coletiva no parque cria um senso de comunidade e pertencimento, unindo pessoas com o mesmo propósito de buscar uma vida mais saudável e equilibrada. Recomendamos o uso de roupas confortáveis, protetor solar e que cada participante traga sua garrafa de água e seu tapete de yoga (ou uma toalha grande). Este evento é uma celebração do autocuidado e um lembrete de que a saúde começa de dentro para fora. Além dos benefícios físicos, você sairá com o espírito renovado e energia positiva para enfrentar o restante do dia. Não há nada como saudar o sol em um espaço aberto, sentindo a brisa no rosto enquanto você estica seus limites e descobre novas capacidades do seu próprio corpo. Venha respirar ar puro, conectar-se com a terra e dar a si mesmo o presente da tranquilidade. A participação é gratuita, reforçando nosso compromisso com o bem-estar acessível para todos. Esperamos você no gramado principal para uma manhã inesquecível de harmonia e luz.', 0, '2025-11-23 08:00:00', '2025-11-23 09:30:00', NOW(), NOW(), NOW(), 6, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 4);

-- 12. Circuito de Corrida
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Circuito de Corrida', 'Calce seus tênis, ajuste o cronômetro e prepare-se para superar seus próprios limites no Circuito de Corrida de Rua 10km. Este evento esportivo de alto nível foi projetado para atletas amadores e profissionais que buscam uma prova desafiadora e bem estruturada no coração da cidade. O percurso foi cuidadosamente traçado para oferecer um misto de trechos planos ideais para quebra de recordes pessoais e leves inclinações que testarão a resistência dos participantes. Durante todo o trajeto, os corredores contarão com pontos de hidratação estratégica, suporte médico de prontidão e o apoio vibrante de bandas musicais espalhadas pelo caminho para manter o ritmo e a motivação no auge. A segurança é nossa prioridade, com vias totalmente bloqueadas e sinalização clara em cada quilômetro. Além da prova principal de 10km, teremos uma caminhada participativa para quem deseja aproveitar a manhã de forma mais relaxada. Na linha de chegada, uma estrutura de arena aguarda os atletas com frutas frescas, isotônicos, massagem pós-prova e, claro, a cobiçada medalha de "finisher" para todos que completarem o percurso. O Circuito de Corrida promove não apenas a saúde física, mas também a integração social e o espírito de superação. Os resultados serão monitorados via chip eletrônico de alta precisão, permitindo que você confira seu tempo oficial e sua colocação em tempo real através do nosso aplicativo. Traga sua família e amigos para torcerem na "Fan Zone" e aproveitarem as ativações dos patrocinadores, que incluem testes de pisada e exposição de novos produtos esportivos. Correr é um ato de liberdade e disciplina, e este evento é a celebração definitiva dessa paixão. Não importa se você está buscando o pódio ou se é sua primeira prova de 10km; o que vale é o suor, a determinação e a alegria de cruzar a linha de chegada. Inscreva-se hoje, comece seus treinos e venha fazer parte desta maré humana de saúde e energia. A cidade será sua pista e a vitória será sua recompensa pessoal!', 16, '2025-12-14 07:00:00', '2025-12-14 10:00:00', NOW(), NOW(), NOW(), 6, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 5);

-- 13. Festival de Cinema Indie
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Festival de Cinema Indie', 'Luzes, câmera e muita originalidade! O Festival de Cinema Indie é o palco das vozes que muitas vezes não chegam ao "mainstream", mas que possuem uma força narrativa avassaladora. Durante cinco dias, as telas serão ocupadas por uma seleção rigorosa de filmes independentes, curtas-metragens e documentários produzidos por cineastas emergentes de diversos cantos do mundo. Este não é apenas um festival de exibição, mas um fórum de resistência cultural onde as histórias são contadas com liberdade criativa total, sem as amarras das grandes produções comerciais. Os temas variam desde dramas sociais profundos e comédias experimentais até ficções científicas lo-fi que desafiam a imaginação. Após cada sessão importante, teremos rodadas de perguntas e respostas (Q&A) com diretores e produtores, permitindo uma troca rica sobre os desafios da produção independente, captação de recursos e a estética cinematográfica contemporânea. O festival também promove workshops técnicos sobre edição, roteiro e cinematografia com smartphones, democratizando as ferramentas de criação audiovisual. O ambiente do festival é vibrante, com um "lounge" para convidados e público discutirem as obras entre uma sessão e outra. Acreditamos que o cinema tem o poder de gerar empatia e mudar perspectivas, por isso, a curadoria focou em obras que trazem diversidade de gênero, raça e origem geográfica. Para os entusiastas, haverá uma competição com prêmios concedidos tanto pelo júri técnico quanto pelo voto popular. Venha com os olhos abertos e a mente pronta para ser desafiada por novas formas de ver o mundo. O cinema independente é o pulmão da sétima arte, onde a experimentação e a paixão superam o orçamento. Garanta seu passaporte para o festival e mergulhe em histórias que ficarão gravadas na sua memória muito depois de as luzes se acenderem. Seja parte deste movimento que celebra a arte pela arte e apoia a nova geração de contadores de histórias. O cinema de verdade espera por você!', 14, '2026-01-20 14:00:00', '2026-01-25 23:00:00', NOW(), NOW(), NOW(), 4, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 10);

-- 14. Torneio de E-Sports
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Torneio de E-Sports', 'A arena está pronta e o lag é zero! Bem-vindo ao Torneio de E-Sports 2025, a competição definitiva onde os melhores pro-players da região se enfrentam em batalhas épicas de League of Legends (LoL) e Counter-Strike (CS). Prepare-se para uma experiência de alto nível tecnológico, com computadores de última geração, telões de LED gigantes transmitindo cada jogada detalhadamente e narração profissional que elevará a adrenalina a cada "pentakill" ou "clutch" decisivo. Este evento não é apenas para quem joga, mas para todos os fãs da cultura gamer. A atmosfera na arena é comparável à de grandes estádios de futebol, com torcidas organizadas, cosplayers icônicos e uma energia contagiante. Além do torneio principal, os visitantes poderão desfrutar de áreas "free-to-play" para testar novos lançamentos, simuladores de corrida em realidade virtual e uma zona retrô com consoles clássicos para os nostálgicos. Teremos também "meet and greet" com influenciadores digitais e streamers famosos, além de palestras sobre carreira nos e-sports, desenvolvimento de jogos e psicologia gamer. Para os competidores, a glória não é o único objetivo: o torneio oferece uma premiação em dinheiro significativa e a chance de ser descoberto por olheiros de grandes organizações. A estrutura conta com praça de alimentação temática, loja de mercadorias exclusivas e uma conexão de internet estável para que o público possa compartilhar cada momento nas redes sociais. Os e-sports representam o ápice da estratégia, reflexos rápidos e trabalho em equipe no século XXI. Venha apoiar seu time favorito, vibrar com as jogadas geniais e mergulhar de cabeça no universo gamer. A competição será acirrada e apenas os mais resilientes chegarão ao topo. Você está pronto para o "GG"? Prepare seu setup emocional e venha viver a emoção dos esportes eletrônicos em sua forma mais pura. Que vençam os melhores!', 16, '2025-12-20 10:00:00', '2025-12-21 22:00:00', NOW(), NOW(), NOW(), 10, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 2);

-- 15. Bazar de Natal
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Bazar de Natal', 'Sinta o espírito natalino ganhar vida no nosso Bazar de Natal, um evento encantador que reúne o melhor do artesanato local, gastronomia típica e entretenimento para toda a família. Imagine caminhar por corredores decorados com luzes cintilantes, enquanto o aroma de canela, chocolate quente e rabanadas frescas preenche o ar. Este bazar é o local perfeito para encontrar presentes únicos e personalizados, fugindo da impessoalidade dos grandes shoppings. Nossos expositores foram selecionados por sua criatividade e qualidade, oferecendo desde brinquedos de madeira feitos à mão, joias autorais, cerâmicas exclusivas até decorações natalinas que transformarão sua casa em um refúgio festivo. Para os pequenos, teremos a oficina do Papai Noel, onde eles podem escrever cartas, participar de oficinas de biscoitos decorados e, claro, tirar a tradicional foto com o bom velhinho. A programação musical inclui corais natalinos e bandas de jazz que interpretarão clássicos da época em versões modernas. Além das compras, o Bazar de Natal é um evento de solidariedade, com parcerias com ONGs locais para arrecadação de brinquedos e alimentos para famílias carentes. É o momento de celebrar a união, a gratidão e a esperança que o final de ano nos traz. A praça de alimentação oferecerá pratos típicos de diferentes culturas, celebrando como o Natal é vivido ao redor do mundo. Traga seus amigos e familiares para passar uma tarde mágica, descobrir o talento dos nossos artesãos e garantir que seu Natal seja repleto de significados e belezas. O Bazar de Natal não é apenas um local de trocas comerciais, mas um ponto de encontro para a comunidade celebrar a vida e a generosidade. Venha se maravilhar com as luzes, emocionar-se com as canções e levar para casa um pedaço desse encanto. Esperamos por você para iniciarmos juntos a época mais bonita do ano!', 0, '2025-12-12 11:00:00', '2025-12-13 19:00:00', NOW(), NOW(), NOW(), 8, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 1);

-- 16. Workshop de Fotografia
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Workshop de Fotografia', 'Aprenda a eternizar momentos com técnica e alma no nosso Workshop de Fotografia: Aprenda a Fotografar com Mestres. Este curso intensivo foi desenhado para quem deseja sair do modo automático e dominar verdadeiramente a linguagem visual através das lentes. Seja você um iniciante com uma câmera DSLR nova ou um entusiasta que utiliza o smartphone, nossos mentores — fotógrafos premiados com décadas de experiência — ensinarão os fundamentos essenciais: exposição, composição, profundidade de campo e a importância da luz natural e artificial. O workshop combina teoria envolvente com muita prática de campo, onde os alunos serão desafiados a capturar retratos expressivos, paisagens urbanas e detalhes macro sob a supervisão dos instrutores. Além da captura, dedicaremos um tempo valioso ao "workflow" digital, explorando técnicas básicas de edição e pós-processamento para dar aquele toque profissional às suas imagens. Discutiremos como desenvolver um olhar crítico e autoral, transformando uma simples foto em uma narrativa poderosa. A fotografia é a arte de observar, e este curso ajudará você a ver o mundo de uma maneira totalmente nova, percebendo cores, texturas e momentos que antes passariam despercebidos. Os participantes receberão material didático exclusivo, certificado de conclusão e terão suas melhores fotos exibidas em uma galeria digital após o evento. O ambiente é de total colaboração, ideal para trocar experiências com outros entusiastas e formar uma rede de contatos na área. Não perca a oportunidade de aprender os segredos profissionais que fazem uma imagem saltar aos olhos. Traga seu equipamento, sua curiosidade e prepare-se para disparar cliques incríveis. O mundo é cheio de beleza esperando para ser capturada; nós ensinaremos você a como enquadrar essa beleza e transformá-la em arte duradoura. Garanta sua vaga, pois os grupos são reduzidos para garantir atenção personalizada a cada aluno!', 18, '2026-02-05 14:00:00', '2026-02-05 18:00:00', NOW(), NOW(), NOW(), 5, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 3);

-- 17. Festa Eletrônica
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Festa Eletrônica', 'Prepare-se para uma noite de pura imersão sonora e visual na nossa Festa Eletrônica: DJs Internacionais. Este evento foi planejado para ser uma jornada pelos subgêneros mais vibrantes da música eletrônica, desde o Techno melódico e o Deep House até os ritmos energéticos do Trance e Tech House. O sistema de som de última geração, aliado a um espetáculo de luzes laser, projeções 3D e pirotecnia controlada, criará uma atmosfera de outro mundo, onde a música e o ambiente se fundem em uma experiência sinestésica total. O lineup conta com nomes de peso da cena mundial, garantindo que a pista de dança permaneça pulsante do primeiro ao último minuto. Além da pista principal, teremos um "chill-out zone" para quem precisa de um momento de descanso, áreas VIP com serviços exclusivos e uma variedade de bares servindo coquetéis premium e bebidas energéticas. A segurança e o bem-estar dos participantes são primordiais, com uma equipe treinada e postos de hidratação espalhados pelo local. Esta festa é um ponto de encontro para a comunidade "raver", celebrando os valores de paz, união, amor e respeito (PLUR). Venha com sua melhor energia, suas roupas mais expressivas e prepare-se para dançar até o amanhecer sob as batidas sintéticas que movem o mundo. A música eletrônica é uma linguagem universal que transcende fronteiras, e nesta noite, estaremos todos conectados pela mesma frequência. Não é apenas uma festa; é uma celebração da liberdade e da modernidade sonora. O sol nascerá enquanto ainda estaremos celebrando a vida e o ritmo. Garanta seu ingresso para a noite mais eletrizante do ano e venha fazer parte desta experiência lendária. A pista está chamando e o grave vai bater forte no seu peito!', 18, '2025-12-27 22:00:00', '2025-12-28 06:00:00', NOW(), NOW(), NOW(), 9, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 1);

-- 18. Peça de Teatro: O Fantasma
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Peça de Teatro: O Fantasma', 'Mergulhe no mistério e no drama com a montagem inédita da peça de teatro "O Fantasma", um clássico do teatro mundial que ganha uma nova e audaciosa interpretação nesta temporada. A história, repleta de reviravoltas góticas e emoções profundas, narra a trajetória de uma figura enigmática que habita as sombras de um antigo teatro, influenciando o destino dos que o rodeiam através de segredos e obsessões. Nossa produção conta com um elenco de primeira linha, cenários suntuosos que recriam a atmosfera decadente e romântica do século XIX, e um figurino impecável que é uma obra de arte à parte. A direção focou na intensidade das atuações e na utilização de efeitos especiais de palco que criarão ilusões óticas e momentos de puro suspense, mantendo a plateia na ponta da poltrona do início ao fim. A trilha sonora original, executada ao vivo por uma pequena orquestra, eleva a carga dramática de cada cena, transportando o espectador para o centro do conflito emocional dos personagens. O teatro é uma experiência viva e "O Fantasma" é a prova de que as grandes histórias nunca morrem; elas apenas se transformam. Esta peça é indicada para quem aprecia dramaturgia de alta qualidade, interpretações viscerais e uma estética visual impecável. Após a apresentação, teremos um breve debate com os atores sobre o processo de construção dos personagens e as nuances da obra original. Venha se emocionar com uma trama que fala sobre amor impossível, o peso do passado e a eterna busca pela redenção. Reserve sua poltrona e prepare-se para uma noite de gala, onde a cortina se abrirá para revelar os segredos mais ocultos da alma humana. "O Fantasma" espera por você, mas lembre-se: no teatro, nada é o que parece ser.', 12, '2026-03-10 20:00:00', '2026-03-10 23:00:00', NOW(), NOW(), NOW(), 4, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 10);

-- 19. Show Acústico
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Show Acústico', 'Sinta a pureza da música em sua forma mais íntima e despojada no Show Acústico: Voz e Violão com artistas locais. Este evento foi concebido para celebrar o talento cru e a poesia dos músicos da nossa região, proporcionando uma noite de conexão direta entre artista e público. Em um ambiente aconchegante, com iluminação suave e uma acústica que valoriza cada detalhe da interpretação, os artistas apresentarão releituras de clássicos da MPB, pop, folk e também composições autorais que muitas vezes não encontram espaço nas grandes rádios. O formato acústico revela a essência de cada canção, onde a voz se torna o instrumento principal e o violão tece harmonias que preenchem o ambiente de forma acolhedora. É a noite perfeita para quem busca um entretenimento de qualidade, onde é possível ouvir as letras e sentir a emoção em cada nota. Entre uma música e outra, os artistas compartilham as histórias por trás de suas criações, criando um clima de "bate-papo" que torna a experiência ainda mais pessoal. O local oferece uma seleção de vinhos, cafés especiais e aperitivos artesanais, perfeitos para acompanhar a suavidade da trilha sonora. Apoiar os artistas locais é fundamental para manter viva a cultura da nossa cidade, e este show é a oportunidade ideal para você descobrir sua nova voz favorita. Traga alguém especial ou venha para desfrutar de um momento de introspecção e beleza. A simplicidade é o último grau da sofisticação, e este show acústico é a prova viva disso. Venha se encantar com a força do talento local e saia com a alma leve e o coração cheio de música. Os ingressos são limitados para manter o clima intimista do evento, então não perca tempo e garanta o seu. Esperamos você para uma noite de pura sensibilidade musical!', 12, '2025-11-30 19:00:00', '2025-11-30 21:00:00', NOW(), NOW(), NOW(), 1, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 6);

-- 20. Palestra sobre Investimentos
INSERT INTO tb_events (title, description, age_restriction, start_date, end_date, approval_date, register_date, last_update_date, category_id, status_id, organizer_id, visibility_id, venue_id)
VALUES ('Palestra sobre Investimentos', 'O cenário econômico global está em constante mutação, e estar preparado é a diferença entre o sucesso financeiro e a estagnação. A palestra "Como Investir em 2026" é um guia prático e estratégico para quem deseja navegar com segurança pelos mercados de capitais no próximo ano. Conduzida por economistas renomados e analistas de mercado experientes, a palestra abordará temas cruciais como a diversificação de carteira em tempos de volatilidade, as perspectivas para a renda fixa e variável, o impacto das criptomoedas nas finanças tradicionais e as oportunidades emergentes em investimentos sustentáveis (ESG). O objetivo é desmistificar o mundo das finanças, oferecendo ferramentas para que tanto investidores iniciantes quanto experientes possam tomar decisões informadas e baseadas em dados. Discutiremos como a inflação global e as taxas de juros podem afetar seus planos de longo prazo e quais são os setores que prometem maior resiliência e crescimento. Além da apresentação técnica, teremos uma sessão prática sobre planejamento financeiro pessoal e sucessório, ajudando você a proteger seu patrimônio e construir um futuro sólido para sua família. O evento contará com um espaço de networking onde os participantes poderão trocar ideias com outros investidores e tirar dúvidas com os palestrantes em um ambiente mais reservado. O conhecimento financeiro é uma das habilidades mais importantes que alguém pode adquirir, proporcionando liberdade e autonomia. Não deixe seu dinheiro parado ou entregue à sorte; aprenda as técnicas utilizadas pelos profissionais para maximizar retornos e mitigar riscos. O futuro financeiro começa com o passo que você dá hoje em direção à educação e à estratégia. Garanta seu lugar nesta palestra transformadora e prepare-se para assumir o controle total do seu destino econômico em 2026. Oportunidades não batem à porta duas vezes; esteja pronto para identificá-las e agir com confiança!', 18, '2026-01-10 19:30:00', '2026-01-10 21:30:00', NOW(), NOW(), NOW(), 5, 2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, 8);

-- Populando Event Images (LocalStack S3 keys)
-- As imagens sao enviadas no init-s3.sh usando a key: events/{event_id}/cover.{ext}
INSERT INTO tb_event_images (event_id, s3_key, ordination)
VALUES
    (1, 'events/1/cover.jpg', 0),
    (2, 'events/2/cover.avif', 0),
    (3, 'events/3/cover.webp', 0);
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
    ec.category_id AS category_id,
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
    SELECT event_id, ARRAY_AGG(s3_key ORDER BY ordination ASC) AS image_keys
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
CREATE INDEX IF NOT EXISTS idx_event_images_event_ordination
    ON tb_event_images(event_id, ordination ASC);

-- para o filtro principal
CREATE INDEX IF NOT EXISTS idx_events_status_visibility
    ON tb_events(status_id, visibility_id);