# TransitaSP — Microsserviços

Global Solution 2026.2 (FIAP) — desafio **CitizenTech**
Disciplina: Microservice and Web Engineering & IT Services — Profª Aparecida F. Castello Rosa

## Integrantes (Equipe Turbo)

| Nome | RM |
|---|---|
| Rafael Vitor de Almeida | 559116 |
| Vitor Silva Batista | 558865 |
| Enzo Demitrius Carvalhaes | 558912 |

## O problema

Quem trabalha na Grande São Paulo sai de casa sem saber qual é o melhor jeito de chegar naquele dia. Para decidir entre metrô, trem, carro ou moto, a pessoa precisa consultar vários aplicativos, e uma falha em uma linha só é percebida quando ela já está na plataforma.

## A solução

O **TransitaSP** recebe a rotina casa-trabalho do usuário, cruza o **status das linhas de metrô e trem** com o trânsito do horário e indica uma só recomendação de trajeto.

Este repositório contém a parte de back-end que mantém o **status das linhas**, a informação da qual a recomendação depende:

- **ms-linhas** guarda as linhas e o status de cada uma (`NORMAL`, `VELOCIDADE_REDUZIDA` ou `PARALISADA`).
- **ms-ocorrencias** registra as ocorrências (falha técnica, objeto na via, manutenção etc.). Abrir, alterar ou encerrar uma ocorrência **muda o status da linha no ms-linhas por comunicação síncrona**.

ODS relacionados: 11 (meta 11.2, transporte seguro e sustentável), 9 e 13.

> **Protótipo acadêmico.** As linhas e ocorrências são dados simulados, cadastrados pela própria API. Não há integração com serviços oficiais de transporte.

## Arquitetura

```
                    ┌──────────────────────┐
                    │  Eureka Server :8761 │   registro e descoberta
                    └──────────▲───────────┘
          ┌────────────────────┼─────────────────────┐
          │                    │                     │
  ┌───────┴───────┐    ┌───────┴────────┐    ┌───────┴────────┐
  │  API Gateway  │    │ ms-ocorrencias │    │   ms-linhas    │
  │     :8082     │    │ porta aleatória│    │ porta aleatória│
  └───────┬───────┘    └───┬────────┬───┘    └───▲────────┬───┘
          │                │        │            │        │
          │  lb://         │        └────────────┘        │
          └──────────▶ os dois      OpenFeign +           │
                                    LoadBalancer +        │
                                    CircuitBreaker +      │
                                    Fallback              │
                    ┌──────▼───────┐              ┌───────▼──────┐
                    │ MySQL :3308  │              │ MySQL :3307  │
                    │db_ocorrencias│              │  db_linhas   │
                    └──────────────┘              └──────────────┘
```

| Componente | Pasta | Porta | Função |
|---|---|---|---|
| Eureka Server | `eureka-server` | 8761 | Registro e descoberta dos serviços |
| API Gateway | `api-gateway` | 8082 | Entrada única; roteia com `lb://` para as instâncias registradas |
| MS Linhas | `ms-linhas` | aleatória | CRUD das linhas e alteração de status |
| MS Ocorrências | `ms-ocorrencias` | aleatória | CRUD das ocorrências; chama o ms-linhas |
| MySQL das linhas | `docker-compose.yml` | 3307 | Banco exclusivo do ms-linhas |
| MySQL das ocorrências | `docker-compose.yml` | 3308 | Banco exclusivo do ms-ocorrencias |

**Tecnologias:** Java 25, Spring Boot 4.0.9, Spring Cloud 2025.1.3 (Eureka, Gateway, OpenFeign, LoadBalancer, Circuit Breaker com Resilience4j), Spring Data JPA, Flyway, MySQL 8.4.6, springdoc-openapi 3.1.0.

### Como a comunicação síncrona funciona

O status de uma linha é sempre **o impacto mais grave entre as ocorrências abertas dela**; sem ocorrência aberta, a linha fica `NORMAL`.

1. `POST /api/v1/ocorrencias` chega ao ms-ocorrencias.
2. Ele chama `GET /api/v1/linhas/{id}` no ms-linhas para confirmar que a linha existe.
3. Grava a ocorrência no próprio banco.
4. Chama `PUT /api/v1/linhas/{id}/status` no ms-linhas com o novo status.

As chamadas usam **OpenFeign** com o nome `ms-linhas`, resolvido no **Eureka** e balanceado pelo **Spring Cloud LoadBalancer**. Não há URL fixa no código nem na configuração. O mesmo recálculo acontece ao atualizar, encerrar ou excluir uma ocorrência.

### Circuit Breaker e Fallback

Cada chamada do `LinhaClient` passa por um Circuit Breaker (Resilience4j). Quando o ms-linhas falha, demora mais de 4 s ou o circuito está aberto, o `LinhaClientFallbackFactory` assume:

- **Consulta** (`GET /api/v1/ocorrencias/{id}`): a ocorrência é devolvida mesmo assim, com `linhaDisponivel: false` e um aviso (HTTP 200).
- **Escrita** (abrir, atualizar, encerrar, excluir): a operação é cancelada, nada é gravado e a API responde **HTTP 503**, para que os dois serviços não fiquem com informações diferentes.

### Camadas de cada microsserviço

```
br.com.fiap.ms.<servico>
├── controller          # Endpoints REST (ResponseEntity); só conversa com a camada service
├── service             # Regras de negócio e CRUD
├── repository          # Acesso ao banco (Spring Data JPA)
├── entities            # Entidades JPA e enums
├── dto/request         # DTOs de entrada (RequestDTO), com validação
├── dto/response        # DTOs de saída (ResponseDTO)
├── mapper              # Conversão entre entidade e DTO
├── exceptions          # Exceções customizadas
│   ├── dto             # CustomErrorDTO, FieldMessageDTO, ValidationErrorDTO
│   └── handler         # GlobalExceptionHandler
├── client              # (ms-ocorrencias) OpenFeign + fallback
└── config              # OpenAPI/Swagger
```

As migrations do Flyway ficam em `src/main/resources/db/migration`: `V1__create_table_...` cria a tabela e `V2__insert_...` faz a carga inicial.

## Como executar

### Pré-requisitos

- **JDK 25** (`java -version` deve mostrar 25)
- **Maven 3.9 ou superior** (`mvn -version`), ou o IntelliJ IDEA
- **Docker** com Docker Compose (`docker compose version`)
- Portas livres: 8761, 8082, 3307 e 3308 (os bancos usam 3307 e 3308 para não conflitar com um MySQL já instalado na 3306)

### 1. Subir os bancos MySQL com Docker

Na raiz do repositório:

```bash
docker compose up -d
```

Aguarde os dois containers ficarem `healthy` (cerca de 30 segundos na primeira vez):

```bash
docker compose ps
```

São criados dois servidores MySQL independentes:

| Container | Porta | Banco | Usuário | Senha |
|---|---|---|---|---|
| `mysql-linhas` | 3307 | `db_linhas` | `transitasp` | `transitasp` |
| `mysql-ocorrencias` | 3308 | `db_ocorrencias` | `transitasp` | `transitasp` |

As tabelas e os dados iniciais são criados pelo Flyway quando cada microsserviço sobe. Não é preciso rodar nenhum script.

### 2. Subir as aplicações

Abra **quatro terminais** na raiz do repositório e execute, **nesta ordem**, esperando cada aplicação terminar de subir antes de iniciar a próxima:

```bash
# Terminal 1 — Eureka Server (porta 8761)
cd eureka-server
mvn spring-boot:run
```

```bash
# Terminal 2 — MS Linhas (porta aleatória)
cd ms-linhas
mvn spring-boot:run
```

```bash
# Terminal 3 — MS Ocorrências (porta aleatória)
cd ms-ocorrencias
mvn spring-boot:run
```

```bash
# Terminal 4 — API Gateway (porta 8082)
cd api-gateway
mvn spring-boot:run
```

**Pelo IntelliJ IDEA:** abra a pasta raiz do repositório (o `pom.xml` da raiz carrega os quatro módulos), defina o SDK do projeto como Java 25 e execute as classes abaixo na mesma ordem:

1. `EurekaServerApplication`
2. `MsLinhasApplication`
3. `MsOcorrenciasApplication`
4. `ApiGatewayApplication`

### 3. Conferir se tudo subiu

Abra **http://localhost:8761**. Devem aparecer `API-GATEWAY`, `MS-LINHAS` e `MS-OCORRENCIAS` com status `UP`.

Depois que o último serviço aparecer no Eureka, aguarde cerca de **15 segundos** antes do primeiro teste. É o tempo de o gateway e o ms-ocorrencias receberem a lista de instâncias. Uma resposta 503 logo após subir significa apenas que esse tempo ainda não passou.

## Como testar

Todas as chamadas passam pelo **API Gateway**, no formato `http://localhost:8082/<nome-do-serviço>/api/v1/...`.

O arquivo [`requests.http`](requests.http) traz o roteiro completo e pode ser executado direto no IntelliJ. Os mesmos passos com `curl`:

```bash
# Linhas da carga inicial (13 linhas; a linha 3 começa com VELOCIDADE_REDUZIDA)
curl http://localhost:8082/ms-linhas/api/v1/linhas

# A linha 1 está NORMAL
curl http://localhost:8082/ms-linhas/api/v1/linhas/1

# Abre uma ocorrência na linha 1 (201 Created + cabeçalho Location)
curl -i -X POST http://localhost:8082/ms-ocorrencias/api/v1/ocorrencias \
  -H "Content-Type: application/json" \
  -d '{"linhaId":1,"tipo":"FALHA_ENERGIA","descricao":"Falha de energia na via","local":"Estacao Se","impacto":"PARALISADA"}'

# A linha 1 agora está PARALISADA: o status foi alterado pela comunicação síncrona
curl http://localhost:8082/ms-linhas/api/v1/linhas/1

# Encerra a ocorrência (use o id devolvido no POST; na primeira execução é 4)
curl -X PUT http://localhost:8082/ms-ocorrencias/api/v1/ocorrencias/4/encerrar

# A linha 1 voltou para NORMAL
curl http://localhost:8082/ms-linhas/api/v1/linhas/1
```

No **PowerShell**, use `curl.exe` no lugar de `curl`. Para o POST, o caminho mais simples é o `requests.http` ou o Swagger UI.

### Endpoints

**ms-linhas** — `http://localhost:8082/ms-linhas/api/v1/linhas`

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/v1/linhas` | Lista as linhas (filtro opcional `?status=`) | 200 |
| GET | `/api/v1/linhas/{id}` | Busca por id | 200, 404 |
| POST | `/api/v1/linhas` | Cadastra (nasce com status `NORMAL`) | 201 + `Location`, 400, 409, 422 |
| PUT | `/api/v1/linhas/{id}` | Atualiza | 200, 400, 404, 409, 422 |
| DELETE | `/api/v1/linhas/{id}` | Exclui (só com status `NORMAL`) | 204, 404, 409 |
| PUT | `/api/v1/linhas/{id}/status` | Altera o status; é o endpoint chamado pelo ms-ocorrencias | 200, 400, 404, 422 |

**ms-ocorrencias** — `http://localhost:8082/ms-ocorrencias/api/v1/ocorrencias`

| Método | Rota | Descrição | Respostas |
|---|---|---|---|
| GET | `/api/v1/ocorrencias` | Lista (filtros opcionais `?status=` e `?linhaId=`) | 200 |
| GET | `/api/v1/ocorrencias/{id}` | Busca por id, com a situação atual da linha | 200, 404 |
| POST | `/api/v1/ocorrencias` | Abre a ocorrência e altera o status da linha | 201 + `Location`, 400, 404, 422, 503 |
| PUT | `/api/v1/ocorrencias/{id}` | Atualiza uma ocorrência aberta e recalcula o status da linha | 200, 400, 404, 409, 422, 503 |
| PUT | `/api/v1/ocorrencias/{id}/encerrar` | Encerra e recalcula o status da linha | 200, 404, 409, 503 |
| DELETE | `/api/v1/ocorrencias/{id}` | Exclui; se estava aberta, recalcula o status da linha | 204, 404, 503 |

Códigos de erro: **400** para JSON malformado ou valor fora da lista permitida, **404** para recurso inexistente, **409** para regra de negócio violada, **422** para campo reprovado na validação (com a lista de campos) e **503** quando o ms-linhas não responde.

Valores aceitos:

- `tipo` da linha: `METRO`, `TREM`
- `status` da linha: `NORMAL`, `VELOCIDADE_REDUZIDA`, `PARALISADA`
- `tipo` da ocorrência: `FALHA_TECNICA`, `FALHA_ENERGIA`, `OBJETO_NA_VIA`, `MANUTENCAO`, `SUPERLOTACAO`, `OUTRO`
- `impacto` da ocorrência: `VELOCIDADE_REDUZIDA`, `PARALISADA`
- `status` da ocorrência: `ABERTA`, `ENCERRADA`

### Swagger UI

O API Gateway publica um **Swagger dinâmico**, montado a partir dos microsserviços registrados no Eureka:

**http://localhost:8082/swagger-ui/index.html**

No canto superior direito, em **Select a definition**, escolha `ms-linhas` ou `ms-ocorrencias`. As requisições executadas pelo Swagger passam pelo gateway. A lista de serviços é atualizada a cada 10 segundos; se um deles ainda não aparecer, recarregue a página.

### Testar o Fallback

1. Pare o **ms-linhas** (Ctrl+C no terminal 2).
2. `curl http://localhost:8082/ms-ocorrencias/api/v1/ocorrencias/1` devolve **200** com `"linhaDisponivel": false` e o aviso de indisponibilidade.
3. Um `POST /api/v1/ocorrencias` devolve **503** e a ocorrência **não** é gravada.
4. Suba o ms-linhas de novo. Em cerca de 15 segundos as chamadas voltam ao normal.

### Testar várias instâncias e o Load Balancer

Os microsserviços usam `server.port=0` e um identificador único por instância, então basta repetir o comando em outro terminal:

```bash
cd ms-linhas
mvn spring-boot:run
```

O Eureka passa a mostrar duas instâncias de `MS-LINHAS`, e as chamadas do gateway e do ms-ocorrencias são distribuídas entre elas.

## Encerrar e limpar

Pare as aplicações com Ctrl+C. Para os bancos:

```bash
docker compose down        # para os containers e mantém os dados
docker compose down -v     # para os containers e apaga os dados (as cargas iniciais voltam na próxima subida)
```

## Problemas comuns

| Sintoma | Causa provável | O que fazer |
|---|---|---|
| `Communications link failure` ao subir um microsserviço | MySQL ainda não está pronto | Confira `docker compose ps` e espere o status `healthy` |
| Porta 3307 ou 3308 em uso | Outro programa usa a porta | Troque a porta à esquerda no `docker-compose.yml` e ajuste a porta em `spring.datasource.url` no `application.properties` do microsserviço |
| 503 no gateway logo após subir | Lista de instâncias ainda não chegou | Aguarde 15 segundos e repita |
| Erro de versão do Java ao compilar | JDK diferente de 25 | Confira `java -version` e a variável `JAVA_HOME` |
