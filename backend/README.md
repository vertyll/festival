# festival — back-end

A Spring Boot API over MongoDB, Redis and S3-compatible storage, serving the festival page and the admin panel. It
signs users in and keeps their tokens ([Authentication](../docs/authentication.md)) and answers errors as translation
keys ([Errors and translations](../docs/architecture.md#errors-and-translations)).

## Layout

The code is split by feature, and each feature keeps its controllers, service, repository and documents together.

| Package    | Holds                                                                       |
|------------|-----------------------------------------------------------------------------|
| `catalog`  | products, categories and attributes                                         |
| `shop`     | orders, addresses and wishlists                                             |
| `lineup`   | artists and stages                                                          |
| `content`  | news and sponsors                                                           |
| `media`    | image uploads to the object storage                                         |
| `settings` | shop settings an admin can change: shipping price, stock visibility         |
| `i18n`     | the message catalogue, its defaults, admin overrides and spreadsheet import |
| `security` | sign-in, sessions and tokens                                                |
| `common`   | errors, localized text, validation and shared types                         |

## Access

`security/SecurityConfig` decides by path: public reads of the catalogue, lineup, content, settings and translations;
`/api/account/**` and placing an order for a signed-in user; `/api/admin/**` for `ADMIN`. Everything else is refused
(`denyAll()`), so a new endpoint is closed until it is listed. There is no OpenAPI description; the controllers are the
reference.

## Accounts

There is no local copy of a person. Orders, addresses and wishlists are keyed by the Keycloak identifier, and the name
and email come from the token, so a change made in Keycloak is visible at once.

## Shop

The cart lives in the browser until an order is placed. Placing one takes the products and prices from the catalogue,
not from the request, and reserves the stock line by line; when a line cannot be reserved, everything reserved so far is
released and no order is written. `FESTIVAL_CHECKOUT_ENABLED` switches ordering off without hiding the shop, and the
settings an admin changes start from `application.shop.initial-settings`.

## Media

Images are uploaded through the admin panel to `POST /api/admin/media` and stored in the object storage (Garage locally,
any S3-compatible service in production). They are served straight from the storage's public address
(`application.media.public-base-url`), never through the back-end.

## Translations

The catalogue ships in `src/main/resources/i18n`. At startup the stored catalogue is brought in line with those files.
An admin can override any message, one at a time or by importing a spreadsheet exported from the panel; an override
must parse as ICU MessageFormat and may use only the placeholders of its default.

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
