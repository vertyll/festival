# Development Setup

## Prerequisites

- Docker or Podman, with Compose
- JDK 25
- Node.js and pnpm

## Start the infrastructure

```bash
docker compose -f docker-compose.local.yml up -d
```

Every `docker compose` command here works verbatim as `podman compose`.

| Service      | Address                                          | Purpose                                         |
|--------------|--------------------------------------------------|-------------------------------------------------|
| MongoDB      | `localhost:27017`                                | the `festival` database                         |
| Redis        | `localhost:6379`                                 | sessions and the shared refresh lock            |
| Keycloak     | `http://localhost:9000` (`admin` / `admin`)      | realm `festival`, imported on start             |
| Garage       | `localhost:3900` (S3), `localhost:3902` (public) | the `festival-media` bucket for images          |
| Garage UI    | `http://localhost:3909`                          | browsing the bucket                             |
| MailDev      | `http://localhost:1080`                          | catches Keycloak's verification and reset mails |
| RedisInsight | `http://localhost:5540`                          | browsing the sessions in Redis                  |

`garage-init` runs once and exits: it turns website hosting on for the bucket, which is how images are served publicly.

Keycloak imports `keycloak/realm-export.json` on its first start, with two accounts:

| Account                 | Password   | Roles           |
|-------------------------|------------|-----------------|
| `admin@festival.local`  | `festival` | `USER`, `ADMIN` |
| `klient@festival.local` | `festival` | `USER`          |

The realm lives in the `keycloak-data` volume afterwards, so a change to the export file only takes effect after
`docker compose -f docker-compose.local.yml down -v`.

## Run the applications

Start the back-end, then the front-end; each README says how, and lists its checks and production settings:
[back-end](../backend/README.md), [front-end](../frontend/README.md).

| Application       | Address                 |
|-------------------|-------------------------|
| The back-end      | `http://localhost:8080` |
| The festival page | `http://localhost:3000` |
| The admin panel   | `http://127.0.0.1:3001` |

## Everything in containers

The `app` profile adds the back-end and both front-end applications to the compose file:

```bash
docker compose -f docker-compose.local.yml --profile app up -d --build
```
