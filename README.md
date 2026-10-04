<p align="center">
    <img alt="" src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
    <img alt="" src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white">
    <img alt="" src="https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white">
    <img alt="" src="https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black">
    <img alt="" src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white">
    <img alt="" src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
    <img alt="" src="https://img.shields.io/badge/Keycloak-00b8e3?style=for-the-badge&logo=keycloak&logoColor=4D4D4D">
    <img alt="" src="https://img.shields.io/badge/Tailwind_CSS-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white">
    <img alt="" src="https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white">
</p>

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

- **Identity provider**: Keycloak (realm `festival`) owns every page that touches a credential: sign-up, sign-in, email
  verification, password reset, two-factor authentication and acceptance of the terms of use. The application never sees
  a password.
- **Pattern**: BFF with Spring Security's OAuth2 client. The festival page and the admin panel sign in with separate
  clients (`festival-page`, `festival-admin`) using the authorization code flow and PKCE; the back-end keeps the tokens
  and the browser holds only the `FESTIVAL_SESSION` cookie (`HttpOnly`, `SameSite=Lax`, `Secure` in production) with a
  CSRF token.
- **Session store**: Redis (Spring Session, `festival:session` namespace).
- **JWT**: on every API request the back-end takes the access token from the session, refreshing it when it is about to
  expire, and verifies it like a resource server: signature (Keycloak's JWKS), issuer, expiry and audience
  (`festival-api`). The request's identity and roles come from that token, so revoking `ADMIN` in Keycloak cuts admin
  access within minutes. Every account gets `USER`; only `ADMIN` opens the admin panel.
- **State**: the back-end is stateless: every request is authorized by the JWT alone, so any instance can serve it. The
  only state is the browser session holding the tokens, and it lives in Redis, outside the application.
- **Token lifecycle**: access tokens live five minutes; every refresh returns a new refresh token and invalidates the
  old one, and concurrent requests of one session share a single refresh. A refresh Keycloak refuses ends the session,
  so a blocked account or a revoked role stops working within minutes. Signing out also ends the Keycloak session.
- **Accounts**: there is no local copy of a person; orders, addresses and wishlists are keyed by the Keycloak
  identifier.

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
