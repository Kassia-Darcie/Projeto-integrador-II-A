# Agenda de Contatos API

Documentação da API REST para gerenciamento de contatos. Desenvolvida com **Spring Boot 4.1.0**, **Spring Data JPA**, **Flyway** e **PostgreSQL**.

---

## 📋 Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Instalação e Setup](#instalação-e-setup)
- [Endpoints](#endpoints)
- [Modelos de Dados](#modelos-de-dados)
- [Tratamento de Erros](#tratamento-de-erros)
- [Importar no Postman/Insomnia](#importar-no-postmaninsomnia)
- [Testes](#testes)

---

## Visão Geral

API especializada em operações CRUD para contatos. Permite:

- ✅ Criar contatos com validações automáticas
- ✅ Listar todos os contatos ou filtrados por email/nome
- ✅ Buscar contato específico por ID
- ✅ Atualizar dados de contatos existentes
- ✅ Remover contatos

**Base URL:** `http://localhost:8080`

---

## Tecnologias

| Tecnologia | Versão | Propósito |
|-----------|--------|----------|
| Java | 21 (OpenJDK) | Linguagem |
| Spring Boot | 4.1.0 | Framework web |
| Spring Data JPA | - | Persistência de dados |
| Flyway | Latest | Migração de banco |
| PostgreSQL | 18+ | Banco de dados |
| Lombok | Latest | Reducir boilerplate |
| Maven | 3.8+ | Gerenciador de dependências |

---

## Instalação e Setup

### Pré-requisitos

```bash
# Verificar Java 21
java -version

# Verificar Maven
mvn --version

# PostgreSQL

docker run -d \
  --name pg-agenda \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=1234 \
  -e POSTGRES_DB=agenda-contatos \
  -p 5432:5432 \
  postgres:18
```


### Executar Aplicação

```bash
# Modo desenvolvimento (com hot reload)
./mvnw spring-boot:run

# Compilar e empacotar
./mvnw clean package

# Executar JAR
java -jar target/agenda-contatos-0.0.1-SNAPSHOT.jar
```

A API estará disponível em: **http://localhost:8080**

### Popular o banco de dados com o script SQL

O repositório contém o arquivo `init_postgres.sql` na raiz do projeto que cria a tabela `contatos` e insere registros de exemplo. Há duas formas comuns de executar esse script:

- Usando o cliente `psql` (no host):

```bash
# se você usa a mesma senha do exemplo (1234):
PGPASSWORD=1234 psql -h localhost -U postgres -d agenda-contatos -f init_postgres.sql
```

- Usando um container Docker do PostgreSQL (supondo que o container foi iniciado com o nome `pg-agenda`):

Opção A — enviar o arquivo para o comando psql no container (recomendado quando o arquivo está no host):

```bash
cat init_postgres.sql | docker exec -i pg-agenda psql -U postgres -d agenda-contatos
```

Opção B — copiar o arquivo para dentro do container e executar lá:

```bash
docker cp init_postgres.sql pg-agenda:/init_postgres.sql
docker exec -it pg-agenda psql -U postgres -d agenda-contatos -f /init_postgres.sql
```

Observações:
- Ajuste o nome do banco, usuário e senha conforme suas variáveis de ambiente ou configuração em `src/main/resources/application.properties`.

---

## Endpoints

### Resumo Geral

| Método | Endpoint | Descrição | Status |
|--------|----------|-----------|--------|
| `POST` | `/contatos` | Criar contato | 201 |
| `GET` | `/contatos` | Listar todos | 200 |
| `GET` | `/contatos?email=...` | Buscar por email | 200 |
| `GET` | `/contatos?nome=...` | Buscar por nome | 200 |
| `GET` | `/contatos/{id}` | Buscar por ID | 200 |
| `PUT` | `/contatos/{id}` | Atualizar | 200 |
| `DELETE` | `/contatos/{id}` | Remover | 204 |

---

### 1️⃣ Criar Contato

```http
POST /contatos HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "nome": "João Silva",
  "telefone": "11999999999",
  "email": "joao@example.com"
}
```

**Resposta (201 Created):**

```json
{
  "id": 1,
  "nome": "João Silva",
  "telefone": "11999999999",
  "email": "joao@example.com"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/contatos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "telefone": "11999999999",
    "email": "joao@example.com"
  }'
```

---

### 2️⃣ Listar Todos os Contatos

```http
GET /contatos HTTP/1.1
Host: localhost:8080
```

**Resposta (200 OK):**

```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "telefone": "11999999999",
    "email": "joao@example.com"
  },
  {
    "id": 2,
    "nome": "Maria Santos",
    "telefone": "21988888888",
    "email": "maria@example.com"
  }
]
```

**cURL:**
```bash
curl -X GET http://localhost:8080/contatos
```

---

### 3️⃣ Buscar Contato por Email

```http
GET /contatos?email=joao@example.com HTTP/1.1
Host: localhost:8080
```

**Resposta (200 OK):**

```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "telefone": "11999999999",
    "email": "joao@example.com"
  }
]
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/contatos?email=joao@example.com"
```

> 💡 **Nota:** Quando `email` e `nome` são fornecidos juntos, `email` tem prioridade.

---

### 4️⃣ Buscar Contatos por Nome (Parcial)

```http
GET /contatos?nome=maria HTTP/1.1
Host: localhost:8080
```

**Resposta (200 OK):**

```json
[
  {
    "id": 2,
    "nome": "Maria Santos",
    "telefone": "21988888888",
    "email": "maria@example.com"
  },
  {
    "id": 3,
    "nome": "Maria Clara",
    "telefone": "21977777777",
    "email": "mariaclara@example.com"
  }
]
```

**cURL:**
```bash
curl -X GET "http://localhost:8080/contatos?nome=maria"
```

> 💡 **Nota:** A busca é case-insensitive e busca por texto parcial.

---

### 5️⃣ Buscar Contato por ID

```http
GET /contatos/1 HTTP/1.1
Host: localhost:8080
```

**Resposta (200 OK):**

```json
{
  "id": 1,
  "nome": "João Silva",
  "telefone": "11999999999",
  "email": "joao@example.com"
}
```

**cURL:**
```bash
curl -X GET http://localhost:8080/contatos/1
```

**Erro (404 Not Found):**
```json
{
  "status": 404,
  "erro": "Contato não encontrado com id: 999"
}
```

---

### 6️⃣ Atualizar Contato

```http
PUT /contatos/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "nome": "João da Silva",
  "telefone": "11888888888",
  "email": "joao.silva@example.com"
}
```

**Resposta (200 OK):**

```json
{
  "id": 1,
  "nome": "João da Silva",
  "telefone": "11888888888",
  "email": "joao.silva@example.com"
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/contatos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva",
    "telefone": "11888888888",
    "email": "joao.silva@example.com"
  }'
```

---

### 7️⃣ Remover Contato

```http
DELETE /contatos/1 HTTP/1.1
Host: localhost:8080
```

**Resposta (204 No Content)**

```bash
# Sem corpo na resposta
```

**cURL:**
```bash
curl -X DELETE http://localhost:8080/contatos/1
```

---

## Modelos de Dados

### ContatoRequest

Objeto enviado ao criar ou atualizar contatos.

```json
{
  "nome": "string (obrigatório, não-vazio)",
  "telefone": "string (obrigatório, exatamente 11 dígitos)",
  "email": "string (obrigatório, deve ser um email válido)"
}
```

### ContatoResponse

Objeto retornado nas respostas.

```json
{
  "id": "number (gerado automaticamente)",
  "nome": "string",
  "telefone": "string",
  "email": "string"
}
```

### Banco de Dados (Tabela: `contatos`)

| Campo | Tipo | Constrains |
|-------|------|-----------|
| `id` | SERIAL | PK, AUTO_INCREMENT |
| `nome` | VARCHAR(60) | NOT NULL, UNIQUE |
| `email` | VARCHAR(60) | NOT NULL, UNIQUE |
| `telefone` | VARCHAR(11) | NOT NULL |

---

## Tratamento de Erros

### Validação (400 Bad Request)

Quando campos não passam na validação:

```json
{
  "status": 400,
  "erros": {
    "telefone": "O telefone deve conter apenas números e ter 11 dígitos",
    "email": "Formato de email inválido"
  }
}
```

**Regras de Validação:**

| Campo | Regra |
|-------|-------|
| `nome` | Não pode ser vazio ou nulo |
| `telefone` | Exatamente 11 dígitos numéricos |
| `email` | Deve ser um email válido e não vazio |

### Recurso Não Encontrado (404 Not Found)

```json
{
  "status": 404,
  "erro": "Contato não encontrado com id: 99"
}
```

---

## Importar no Postman/Insomnia

### 📮 Postman

1. Abra o **Postman**
2. Clique em **Import** (canto superior esquerdo)
3. Selecione a aba **Link**
4. Cole a URL da coleção ou importe o arquivo `agenda-contatos-postman.json`
5. Clique em **Import**

### 🦅 Insomnia

1. Abra o **Insomnia**
2. Vá em **Create** → **Import from File**
3. Selecione o arquivo `agenda-contatos-postman.json`
4. Clique em **Scan**
5. A coleção será criada automaticamente

**Arquivo de coleção disponível em:** `./postman-collection.json`

---

## Testes

### Executar Todos os Testes

```bash
./mvnw test
```

### Executar Teste Específico

```bash
./mvnw test -Dtest=ContatoControllerTest
./mvnw test -Dtest=ContatoServiceImplTest
```

### Cobertura de Testes

```bash
./mvnw clean test jacoco:report
# Relatório gerado em: target/site/jacoco/index.html
```

**Testes incluídos:**

- ✅ Controller: 11 testes (POST, GET, PUT, DELETE, validações)
- ✅ Service: 12 testes (lógica de negócio, exceções)
- **Total:** 23 testes integrados

---

## Estrutura do Projeto

```
agenda-contatos/
│
├── src/main/java/com/kassia/agendacontatos/
│   ├── AgendaContatosApplication.java      # Main
│   ├── config/
│   │   └── GlobalExceptionHandler.java      # Tratamento global de erros
│   ├── controller/
│   │   └── ContatoController.java           # REST endpoints
│   ├── dto/
│   │   ├── ContatoRequest.java              # DTO de entrada
│   │   └── ContatoResponse.java             # DTO de saída
│   ├── entity/
│   │   └── Contato.java                    # Entidade JPA
│   ├── exception/
│   │   └── RecursoNaoEncontrado.java       # Exceção customizada
│   ├── repository/
│   │   └── ContatoRepository.java           # Interface CRUD
│   └── service/
│       ├── ContatoService.java              # Contrato de serviço
│       └── impl/
│           └── ContatoServiceImpl.java       # Implementação
│
├── src/main/resources/
│   ├── application.properties                # Config da aplicação
│   └── db/migration/
│       └── V1__create_table_contatos.sql    # Migration Flyway
│
├── src/test/java/
│   └── com/kassia/agendacontatos/
│       ├── AgendaContatosApplicationTests.java
│       ├── controller/
│       │   └── ContatoControllerTest.java
│       └── service/
│           └── impl/
│               └── ContatoServiceImplTest.java
│
├── pom.xml                                   # Dependências Maven
├── README.md                                 # Esta documentação
└── postman-collection.json                   # Coleção Postman
```

---

## 🚀 Início Rápido

```bash
# 1. Clone o repositório
git clone <seu-repo>
cd agenda-contatos

# 2. Inicie PostgreSQL
docker run -d --name pg-agenda \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=1234 \
  -e POSTGRES_DB=agenda-contatos \
  -p 5432:5432 postgres:18

# 3. Execute a aplicação
./mvnw spring-boot:run

# 4. Teste um endpoint
curl http://localhost:8080/contatos

# 5. Importe a coleção no Postman (opcional)
# postman-collection.json
```

---

## 📝 Notas Importantes

- **Campos Únicos:** `nome` e `email` não podem ser duplicados
- **Telefone:** Exatamente 11 dígitos (com DDD)
- **Filtro por Nome:** Case-insensitive e busca parcial
- **Filtro por Email:** Busca exata
- **Prioridade:** Se `email` e `nome` são enviados, `email` é priorizado
- **Migração:** Flyway cria tabelas automaticamente ao iniciar



