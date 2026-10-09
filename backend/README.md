# festival — back-end

A Spring Boot API over MongoDB, Redis and S3-compatible storage, serving the festival page and the admin panel. It
signs users in and keeps their tokens ([Authentication](../docs/authentication.md)) and answers errors as translation
keys ([Errors and translations](../docs/architecture.md#errors-and-translations)).

## Layout

The code is split by feature, and each feature keeps its controllers, service, repository and documents together.

| Package    | Holds                                                                     |
|------------|---------------------------------------------------------------------------|
| `catalog`  | products, categories and attributes                                       |
| `shop`     | orders, addresses and wishlists                                           |
| `lineup`   | artists and stages                                                        |
| `content`  | news and sponsors                                                         |
| `media`    | image uploads to the object storage                                       |
| `settings` | shop settings an admin can change: shipping price, stock visibility       |
| `i18n`     | the message catalog, its defaults, admin overrides and spreadsheet import |
| `security` | sign-in, sessions and tokens                                              |
| `common`   | errors, localized text, validation and shared types                       |

## Access

`security/SecurityConfig` decides by path: the public reads are listed in `PUBLIC_READ_ENDPOINTS`, `/api/admin/**`
needs `ADMIN`, and the account and ordering paths a signed-in user. Everything else is refused (`denyAll()`), so a new
endpoint is closed until it is listed there.

## Accounts

There is no local copy of a person. Orders, addresses and wishlists are keyed by the Keycloak identifier, and the name
and email come from the token, so a change made in Keycloak is visible at once.

## Mechanisms

- [Media storage](docs/mechanisms/media-storage.md) – Where uploaded files are written, and how they reach the browser.
- [Order placement](docs/mechanisms/order-placement.md) – What happens between a cart in the browser and a stored order.
- [Token refresh](docs/mechanisms/token-refresh.md) – How the session keeps a valid access token without signing the
  user out when requests race.
- [Translation catalog](docs/mechanisms/translation-catalog.md) – Where the text behind every message key comes from,
  and how an administrator's edits survive a deployment.

## Running it

Start the infrastructure first ([Development Setup](../docs/development-setup.md)), then:

```bash
./mvnw spring-boot:run
```

The `local` profile is the default and points at the local containers; it listens on `http://localhost:8080`.

## Checks

```bash
./mvnw spotless:apply   # format
./mvnw verify           # Spotless, PMD, SpotBugs and the tests
```

The tests start MongoDB and Redis with Testcontainers, so Docker or Podman must be running.

## Production

The `prod` profile takes its settings from the environment:

| Variable                                                                         | Purpose                                                                         |
|----------------------------------------------------------------------------------|---------------------------------------------------------------------------------|
| `MONGODB_URI`                                                                    | MongoDB                                                                         |
| `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`                                     | Redis                                                                           |
| `KEYCLOAK_REALM_URL`                                                             | the realm                                                                       |
| `KEYCLOAK_PAGE_CLIENT_SECRET`, `KEYCLOAK_ADMIN_CLIENT_SECRET`                    | the secrets of `festival-page` and `festival-admin`                             |
| `FESTIVAL_PAGE_URL`, `FESTIVAL_ADMIN_URL`                                        | the public addresses of the two front-end applications                          |
| `FESTIVAL_CHECKOUT_ENABLED`                                                      | whether orders can be placed                                                    |
| `S3_ENDPOINT`, `S3_REGION`, `S3_BUCKET`, `S3_ACCESS_KEY`, `S3_SECRET_ACCESS_KEY` | the object storage for images                                                   |
| `S3_PUBLIC_BASE_URL`                                                             | where the stored images are served from                                         |
| `INTERNAL_CA_CERT`                                                               | the CA that signs the TLS certificates of MongoDB, Redis and the object storage |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`          | SMTP                                                                            |
