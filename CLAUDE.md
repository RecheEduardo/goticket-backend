# GoTicket Backend — Arquitetura, Segurança e Domínio

## 1. Estrutura de diretórios real
Package raiz: `tech.goticket.backendapi`. Organização por feature (não por camada técnica global):

```text
src/main/java/tech/goticket/backendapi/
├── BackendapiApplication.java
├── admin/                  # Domínio: administradores da plataforma
├── client/                 # Domínio: cliente final (comprador)
├── event/
│   ├── controller/
│   ├── dto/
│   ├── repository/
│   ├── service/
│   ├── view/               # Projections / specifications JPA
│   └── Event.java, EventDate.java, EventSector.java, ...
├── organizer/              # Domínio: produtor/organizador de eventos
├── sector/                 # Setor (compartilhado entre venue e event)
├── ticket/                 # Ingressos, lotes, alotments, status
├── user/                   # Autenticação, usuários, refresh tokens
│   └── token/              # AuthTokenService, RefreshToken*
├── venue/                  # Locais (espaços físicos) e seus setores
└── shared/                 # Cross-cutting:
    ├── config/             # SecurityConfig, GlobalExceptionHandler, ApiError, AdminUserConfig, CustomAuthenticationEntryPoint
    ├── exception/          # Hierarquia de exceções de domínio
    ├── job/                # Jobs agendados (ex: RefreshTokenCleanupJob, MaterializedViewRefreshJob)
    ├── model/              # Modelos compartilhados (ex: Status)
    ├── storage/            # FileUpload + FileStorageService (S3)
    └── utils/              # DocumentValidator, etc.
```

Regra: domínio novo entra como pasta irmã (`/promo`, `/coupon`, etc.). Não criar `core/` paralelo a `shared/` — usar o que já existe.

## 2. Convenções de camada (estado atual + direção)

### O que já é regra hoje
- **DTOs como entrada e saída na maioria dos endpoints.** Use Java `record` (padrão dos DTOs em `*/dto/`).
- **Tratamento global de erros** via `shared/config/GlobalExceptionHandler` (`@RestControllerAdvice`). Para um novo tipo de falha, lance uma exceção de `shared/exception/` e (se for um caso novo) registre o handler lá.
- **Autorização por anotação:** `@PreAuthorize("hasAnyAuthority('SCOPE_ADMIN', 'SCOPE_ORGANIZER')")` no método da controller. Os escopos vêm do claim `scope` do JWT.
- **Identidade do usuário autenticado:** extrair de `Authentication.getName()` (que é o `sub` do JWT, um UUID). **Nunca** receber `userId` no body em operações sensíveis.

### Direção desejada (dívida técnica conhecida)
Algumas regras abaixo NÃO refletem 100% do código atual, mas devem guiar **código novo** e refatorações sob demanda. Não refatore arquivos existentes só por causa delas.
- **Controller magra.** Código novo deve delegar a lógica para o service. `EventController.createNewEvent` é o anti-exemplo atual (orquestra busca, validação e construção da entidade no controller) — evite repetir esse padrão.
- **Não vazar `@Entity` em retornos públicos.** Existem hoje endpoints que retornam `ResponseEntity<Event>` direto (ex: `GET /events/{id}/details`, `PATCH /events/{id}`). Em código novo, prefira um DTO de resposta.
- **Injeção via construtor** (Lombok `@RequiredArgsConstructor` em `final` fields). Há controllers usando `@Autowired` em campo (`EventController`, etc.) — mantidas por ora; código novo usa construtor.

## 3. Segurança
- **Fechado por padrão.** Em `shared/config/SecurityConfig`, qualquer rota não listada em `permitAll()` exige JWT válido. Endpoints públicos atuais: `POST /login`, `POST /auth/refresh`, `POST /clients`, `POST /organizers`, `GET /events`, `GET /events/{id}`, `GET /event-categories`, `GET /event-categories/{id}`, `GET /venues/{id}`, `GET /venues/*/sector-map`, `GET /error`. Para tornar uma rota pública, edite `SecurityConfig` explicitamente.
- **CORS está liberado para `http://localhost:5173`** (Vite dev). Os comentários `TODO: review em prod` em `SecurityConfig` são reais — não removê-los sem revisar prod.
- **Refresh token rotativo** com detecção de reuso (`RefreshTokenReuseException` → revoga a família via `RefreshTokenFamilyRevoker`). Não introduzir um fluxo de refresh paralelo.
- **Senhas com BCrypt** (`bCryptPasswordEncoder` bean). Nunca persista senha em claro.

## 4. Storage e arquivos
- Uploads (banner de evento, imagens, etc.) passam por `shared/storage/FileStorageService` apontando para S3 (LocalStack em dev). Nada de `java.io.File` local.
- O endpoint `PUT /events/{id}/images` é multipart (`metadata` JSON + `newImages` lista). Mantenha esse contrato; mudanças nele afetam o frontend.

## 5. Tratamento de erros
Formato real do payload (definido em `shared/config/ApiError`):
```json
{ "timestamp": "...", "code": 400, "status": "BAD_REQUEST", "errors": ["mensagem"] }
```
Ao adicionar nova exceção:
1. Crie a classe em `shared/exception/` (ou subpasta de domínio dentro dela).
2. Adicione um `@ExceptionHandler` em `GlobalExceptionHandler` retornando o `ApiError` com o status apropriado.
3. **Não** introduza RFC 7807 / `ProblemDetail` agora — quebraria o consumidor.

## 6. Banco e migrações
- Não há Flyway/Liquibase. Schema é gerenciado pelo JPA (configuração via `application.properties`).
- Se um campo novo for necessário, adicione na entidade e teste localmente. Não adicione Flyway sem combinar — é uma decisão de stack.

## 7. Jobs agendados
Existem em `shared/job/`. Se for criar outro, siga o mesmo padrão (anotações `@Scheduled`, classe focada em uma única responsabilidade). Lembre que `BackendapiApplication` precisa habilitar scheduling — verifique antes de adicionar o primeiro job de uma categoria nova.
