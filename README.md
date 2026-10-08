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

- **Identity provider**: Keycloak (realm `festival`); the application never sees a password.
- **Pattern**: BFF with Spring Security's OAuth2 client; the browser holds only a session cookie.
- **Session store**: Redis (Spring Session).
- **JWT**: verified on every request; a client without a browser calls the API with a Bearer token.
- **Details**: [Authentication](./docs/authentication.md).

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
