# festival — front-end

Two Next.js applications in one pnpm workspace, orchestrated by Turborepo. Neither holds a token: sign-in and the
session belong to the back-end ([Authentication](../docs/authentication.md)).

## Layout

| Path              | Holds                                                                     |
|-------------------|---------------------------------------------------------------------------|
| `apps/page`       | the festival page and shop; the cart lives in the browser (`lib/cart.ts`) |
| `apps/admin`      | the admin panel                                                           |
| `packages/shared` | the HTTP client, the session, translations, formatting and validation     |

Each application forwards `/api/*`, `/oauth2/*`, `/login/oauth2/*` and `/logout` to the back-end at
`BACKEND_INTERNAL_URL` (`next.config.base.mjs`), so every call is same-origin and carries the session and CSRF cookies.

## Text

Every label and every error is a key of the back-end's catalogue, formatted with `use-intl`
([Errors and translations](../docs/architecture.md#errors-and-translations)). A failed call is an `ApiError`:
show `userMessage` for the whole request and `fieldErrors` next to the form's inputs. The language is kept in the
`NEXT_LOCALE` cookie, which the back-end also passes to Keycloak.

## Running it

Start the infrastructure and the back-end first ([Development Setup](../docs/development-setup.md)), then:

```bash
pnpm install
pnpm dev          # both applications
pnpm dev:page     # only the page
pnpm dev:admin    # only the panel
```

| Application       | Address                 |
|-------------------|-------------------------|
| The festival page | `http://localhost:3000` |
| The admin panel   | `http://127.0.0.1:3001` |

Each reads `BACKEND_INTERNAL_URL` from its `.env.development`. The panel runs on `127.0.0.1`, not `localhost`: cookies
are scoped to the host, not the port, so this keeps the page's and the panel's sessions apart, and Keycloak's
`festival-admin` client accepts only that address as a redirect.

## Checks

```bash
pnpm format
pnpm lint
pnpm typecheck
pnpm check:translations   # every key in both languages
```

## Production

Each image is built for one application (`APP=page` or `APP=admin`) with `BACKEND_INTERNAL_URL`, the back-end's address
inside the cluster.
