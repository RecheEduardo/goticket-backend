# GoTicket API

Esta é a API Back-End da aplicação GoTicket, responsável por lidar com ações do sistema, segurança, autenticação e middlewares.

## Tecnologias e Configurações
| Recurso        | Configuração | Detalhes no Projeto        |
|--------------|-------|---------------|
| Linguagem   | Java 25    | Versão configurada no pom.xml.     |
| Framework  | Spring Boot    | Utilizado para construir a API. |
| Banco de Dados | PostgreSQL    | Configurado no application.properties. |
| Build Tool  | Apache Maven    | Inclui o Maven Wrapper(mvnw, mvnw.cmd). |
| IDE recomendada  | IntelliJ IDEA Community    | Sugerido para desenvolvimento Java/Spring. |

## Pré-requisitos

Para configurar e rodar o projeto localmente, você deve ter os seguintes softwares instalados e configurados:
1. Java Development Kit (JDK) 25
2. PostgreSQL (Servidor de banco de dados)
3. Git
4. Apache Maven (Opcional, pois o projeto inclui o Wrapper Maven para gerenciamento de dependências e build.)

## Configuração do Ambiente Local
1. Configuração do Banco de Dados PostgreSQL
    - **Crie o Banco de Dados:** Certifique-se de que o PostgreSQL esteja rodando (geralmente na porta 5432) e crie um banco de dados com o nome exato configurado na aplicação:
    ```
    CREATE DATABASE "GoTicketDB";
    ```
    - **Credenciais de conexão:** O projeto está pré-configurado para as seguintes credenciais, localizadas em `goticket-backend/src/main/resources/application.properties`:

    ```
    spring.datasource.url=jdbc:postgresql://localhost:5432/GoTicketDB
    spring.datasource.username=**seu-usuario**
    spring.datasource.password=**sua-senha**
    ```
    **IMPORTANTE:** Caso seu usuário (spring.datasource.username) ou senha (spring.datasource.password) do PostgreSQL sejam diferentes, ajuste-os no arquivo application.properties antes de rodar a aplicação.
    - **Inicialização de Dados:** O Spring Boot está configurado para executar o script data.sql na inicialização (`spring.sql.init.mode=always`), que popula as tabelas de roles (`ADMIN`, `ORGANIZER`, `CLIENT`) e status para o sistema.

2. Configuração das Chaves JWT

    O projeto utiliza JWT (JSON Web Tokens) para autenticação. As chaves pública (`app.pub`) e privada (`app.key`) já estão incluídas no diretório `src/main/resources` e são carregadas automaticamente pelo Spring Security e JWT Decoder/Encoder. Nenhuma ação manual é necessária.

## Como Rodar a Aplicação

1. Clonar e acessar
    ```
    git clone <URL_DO_SEU_REPOSITORIO>
    cd goticket-backend
    ```
2. Executar via IntelliJ IDEA

    1. Abra o IntelliJ IDEA.
    2. Selecione Open e navegue até a pasta `goticket-backend`.
    3. O IntelliJ deve reconhecer o projeto Maven automaticamente.
    4. Localize a classe principal `BackendapiApplication.java` (em `src/main/java/tech/goticket/backendapi/`).
    5. Clique no botão 'Run' (ícone verde de 'play') ao lado do método `main` ou na barra de ferramentas.

    A API estará disponível por padrão em `http://localhost:8080`.


## Credenciais Iniciais

O componente AdminUserConfig cria automaticamente um usuário administrador se ele não for encontrado no banco de dados na primeira inicialização.
| Campo        | Valor |
|--------------|-------|
| Email   | `admin@admin.com` |
| Senha  | `123` (Será codificada com BCrypt antes de ser salva) |
| Role | `ADMIN` |

Use estas credenciais para realizar a autenticação e obter um token de acesso através do endpoint `/login`.

## Observações importantes para uso de endpoints

- Para todos os endpoints que necessitem de alguma `ROLE` atribuída ao usuário que está realizando a request, é necessário informar o token de acesso obtido no endpoint de `/login` na seção de `Headers` da requisição da seguinte maneira:

    ```
    Authorization | Bearer <token adquirido no login> 
    ```

- As requisições `GET`,`POST` e `DELETE` possuem o header `Content-Type` definido por padrão como `application/json`. Contudo, as requisições `PATCH` definidas na aplicação precisam que o `Content-Type` seja definido como `application/merge-patch+json`.

- Link do workspace no Postman para acesso às requests pré-montadas: https://app.getpostman.com/join-team?invite_code=f8844a6a152d27d63065c140f59a9f8fe4969b2340405072e22d98bf59f49762&target_code=9ed7f0c37437cb80c5b860aa5467d861
