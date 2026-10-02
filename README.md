# Sunset Festival

Strona festiwalu muzycznego ze sklepem i panel administracyjny.

Link: https://festival-page.vertyll.dev

## Struktura repozytorium

| Katalog                              | Opis                                                                                       |
|--------------------------------------|--------------------------------------------------------------------------------------------|
| `backend/`                           | Spring Boot 4.1, Java 25 - API                                                             |
| `frontend/packages/shared/`          | `@festival/shared`: typy modeli API, klient HTTP z CSRF, sesja, walidacja, formaty         |
| `frontend/apps/page/`                | Strona festiwalu i sklep                                                                   |
| `frontend/apps/admin/`               | Panel administracyjny                                                                      |
| `frontend/Dockerfile`                | Wieloetapowy obraz dla obu front-endów (`--build-arg APP=page\|admin`)                     |
| `docker-compose.local.yml`, `infra/` | Lokalnie: MongoDB, Garage, Keycloak, maildev, opcjonalnie cały system (profil `app`)       |
| `keycloak/`                          | Realm `festival` importowany przez lokalny Keycloak                                        |
| `.github/workflows/`                 | CI/CD                                                                                      |

## Back-end

### Stos technologiczny

- Spring Boot.
- Java.
- Maven.
- MongoDB.
- JUnit.
- Mockito.
- Lombok.
- Spring Security.
- Spring Data MongoDB.
- Spring Web.
- Keycloak.

### Budowanie i jakość

```bash
cd backend
./mvnw spotless:apply   # formatowanie
./mvnw verify           # kompilacja z Error Prone/NullAway, testy, Spotless, PMD, SpotBugs
```

> [!IMPORTANT]
>
> Polecenie `verify` wymaga środowiska skonteneryzowanego dla Testcontainers.

### Moduły

| Pakiet                                 | Odpowiedzialność                                                        |
|----------------------------------------|-------------------------------------------------------------------------|
| `security`                             | Spring Security, logowanie przez Keycloak (OIDC), role, CSRF, `/api/me` |
| `catalog.{product,category,attribute}` | Produkty z opcjami i wariantami, kategorie, atrybuty                    |
| `lineup.{artist,stage}`                | Artyści i sceny                                                         |
| `content.{news,sponsor}`               | Newsy i sponsorzy                                                       |
| `settings`                             | Ustawienia sklepu                                                       |
| `shop.{address,wishlist,order}`        | Adres dostawy, lista życzeń, zamówienia                                 |
| `media`                                | Upload zdjęć do Garage                                                  |
| `i18n`                                 | Tłumaczenia interfejsu w MongoDB (ICU), edycja w panelu                 |
| `common`, `config`                     | Błędy (RFC 9457), walidacja, konfiguracja Mongo/Jackson/zegara          |

### Profile i konfiguracja

Domyślny profil to `local`; obraz Dockera ustawia `prod` (`SPRING_PROFILES_ACTIVE=prod`).

| Plik                           | Zawartość                                                            |
|--------------------------------|----------------------------------------------------------------------|
| `application.properties`       | wspólna konfiguracja, bez zmiennych środowiskowych                   |
| `application-local.properties` | pełna konfiguracja lokalna (usługi z `docker-compose.local.yml`)     |
| `application-prod.properties`  | same odwołania `${...}` do zmiennych środowiskowych (tabela poniżej) |

### Logowanie i role

Rejestracją, logowaniem, weryfikacją e-maila, resetem hasła, 2FA i akceptacją regulaminu zajmuje się Keycloak (realm
`festival`). Strona i panel logują się osobnymi klientami (`festival-page`, `festival-admin`) przepływem authorization
code z PKCE; back-end trzyma tokeny w sesji i daje przeglądarce tylko ciasteczko `FESTIVAL_SESSION`. Język wybrany na
stronie (`NEXT_LOCALE`) trafia na strony Keycloaka jako `ui_locales`.

Role są rolami realmu: każde konto dostaje `USER`, a `ADMIN` nadaje się w konsoli Keycloaka
(*Users → konto → Role mapping*). Do panelu wchodzi tylko konto z `ADMIN`. Każde żądanie do `/api/admin/**` sprawdza
rolę w aktualnym tokenie dostępu, odświeżanym w razie potrzeby, więc odebranie roli albo zakończenie sesji w Keycloaku
odcina dostęp najpóźniej po wygaśnięciu tokenu, bez czekania na koniec sesji panelu. Wylogowanie
(`POST /logout`) kończy też sesję w Keycloaku.

Lokalny realm (`keycloak/realm-export.json`) ma dwa konta z hasłem `festival`: `admin@festival.local` (`ADMIN`) i
`klient@festival.local`.

Zmienne środowiskowe profilu `prod`:

| Zmienna                                   | Opis                                                             |
|-------------------------------------------|------------------------------------------------------------------|
| `MONGODB_URI`                             | np. `mongodb://localhost:27017/festival`                         |
| `FESTIVAL_PAGE_URL`, `FESTIVAL_ADMIN_URL` | publiczne adresy front-endów (powrót z Keycloaka)                |
| `KEYCLOAK_REALM_URL`                      | adres realmu, np. `https://keycloak.vertyll.dev/realms/festival` |
| `KEYCLOAK_PAGE_CLIENT_SECRET`             | sekret klienta `festival-page`                                   |
| `KEYCLOAK_ADMIN_CLIENT_SECRET`            | sekret klienta `festival-admin`                                  |
| `FESTIVAL_CHECKOUT_ENABLED`               | czy można składać zamówienia (`true`/`false`)                    |
| `S3_ENDPOINT`, `S3_REGION`, `S3_BUCKET`   | Garage: API S3, region (`s3_region` z `garage.toml`), bucket     |
| `S3_ACCESS_KEY`, `S3_SECRET_ACCESS_KEY`   | klucz Garage z uprawnieniem zapisu do bucketa                    |
| `S3_PUBLIC_BASE_URL`                      | publiczny adres bucketa (web endpoint Garage)                    |
| `INTERNAL_CA_CERT`                        | CA klastra (TLS do MongoDB i Garage): `file:/tls/ca.crt`         |

## Front-endy

### Stos technologiczny

- Next.js.
- React.
- Node.js.
- Tailwind CSS.
- styled-components.

### Budowanie i jakość

Oba front-endy i kod współdzielony (`packages/shared`) to monorepo pnpm zarządzane przez Turborepo.

```bash
cd frontend
pnpm install
pnpm dev:page           # http://localhost:3000
pnpm dev:admin          # http://127.0.0.1:3001
pnpm build              # turbo build obu aplikacji
pnpm lint && pnpm typecheck && pnpm format:check && pnpm check:translations
```

> [!NOTE]
>
> Adres back-endu dla `next dev` znajduje się w `apps/*/.env.development`.

## Uruchomienie lokalne

> [!IMPORTANT]
>
> **Wymagania**: Docker, Java 25, Node.js 24 (pnpm przez Corepack).

```bash
docker compose -f docker-compose.local.yml up -d   # MongoDB :27017, Garage :3900 (S3), :3902 (publiczny odczyt) i konsola :3909, Keycloak :9000 (admin/admin), maildev :1025/:1080

cd backend && ./mvnw spring-boot:run                                # :8080
cd frontend && pnpm install && pnpm dev:page                         # http://localhost:3000
cd frontend && pnpm dev:admin                                       # http://127.0.0.1:3001
```

Albo cały system w kontenerach: `docker compose -f docker-compose.local.yml --profile app up -d --build`.

## Zrzuty ekranu

| Strona                           | Panel                             |
|----------------------------------|-----------------------------------|
| ![](docs/screenshots/page/1.png) | ![](docs/screenshots/admin/1.png) |
| ![](docs/screenshots/page/2.png) | ![](docs/screenshots/admin/2.png) |
| ![](docs/screenshots/page/3.png) | ![](docs/screenshots/admin/3.png) |
