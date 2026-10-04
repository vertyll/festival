## Project Assumptions

Music festival website with a shop, and an admin panel to run both.

## Link: https://festival-page.vertyll.dev

## Technology Stack

### Back-end:

- Spring Boot.
- Java.
- Maven.
- MongoDB.
- Redis (Spring Session).
- Garage (S3-compatible storage for images).
- JUnit.
- Mockito.
- Testcontainers.
- Lombok.
- Spring Security.
- Spring Data MongoDB.
- Spring Web.
- Spring Mail.

### Front-end:

- Next.js.
- React.
- TypeScript.
- Tailwind CSS.
- styled-components.
- pnpm workspaces with Turborepo: the festival page (`apps/page`), the admin panel (`apps/admin`) and shared code
  (`packages/shared`).

### Authentication:

- Keycloak (realm `festival`) handles sign-up, sign-in, email verification, password reset, two-factor authentication
  and acceptance of the terms of use.
- The page and the admin panel sign in with separate clients (`festival-page`, `festival-admin`) using the
  authorization code flow with PKCE. The back-end keeps the tokens in its session, stored in Redis, and gives the
  browser only the `FESTIVAL_SESSION` cookie (`HttpOnly`, `SameSite=Lax`, `Secure` in production) with a CSRF token.
- Every account gets the `USER` role; only `ADMIN` opens the admin panel. Admin requests check the role in the current
  access token, so revoking it in Keycloak cuts access within minutes.
- Locally, `docker-compose.local.yml` runs MongoDB, Redis, RedisInsight (`:5540`, connected to Redis), Garage, Keycloak
  on `:9000` (admin/admin) and maildev. The realm from `keycloak/realm-export.json` has two accounts with the password
  `festival`: `admin@festival.local` (`ADMIN`) and `klient@festival.local`.

### Core back-end:

- Maven build system.
- The application has an exception handling mechanism (RFC 9457 problem details).
- The application has a logging mechanism.
- The application has separate environments for local and prod.
- The application has a dedicated configuration file.
- The application has RBAC (Role Based Access Control).
- The application has translations stored in MongoDB (ICU MessageFormat), editable in the admin panel.
- The application has image uploads to S3-compatible storage.
- And many other features that can be found in the application code.

### Core front-end:

- Monorepo with shared types, HTTP client with CSRF, session handling and validation.
- The application has separate environments for local and prod.
- Polish and English, with translations served by the back-end.
- And many other features that can be found in the application code.

### Other:

- Docker for development environment.
- PMD for static code analysis.
- SpotBugs for static code analysis.
- JSpecify for null-safety annotations.
- NullAway for null-safety checks.
- Error Prone for static code analysis.
- Spotless for code formatting.
- ESLint and Prettier for the front-end.

## Preview Screenshots

### Festival page

![Project View](docs/screenshots/page/1.png)
![Project View](docs/screenshots/page/2.png)
![Project View](docs/screenshots/page/3.png)
![Project View](docs/screenshots/page/4.png)
![Project View](docs/screenshots/page/5.png)
![Project View](docs/screenshots/page/6.png)

### Admin panel

![Project View](docs/screenshots/admin/1.png)
![Project View](docs/screenshots/admin/2.png)
![Project View](docs/screenshots/admin/3.png)
![Project View](docs/screenshots/admin/4.png)
![Project View](docs/screenshots/admin/5.png)
