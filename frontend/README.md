# festival — front-end

Two Next.js applications in one pnpm workspace, orchestrated by Turborepo. Neither holds a token: sign-in and the
session belong to the back-end ([Authentication](../docs/authentication.md)).

## Layout

| Path              | Holds                                                                     |
|-------------------|---------------------------------------------------------------------------|
| `apps/page`       | the festival page and shop; the cart lives in the browser (`lib/cart.ts`) |
| `apps/admin`      | the admin panel                                                           |
| `packages/shared` | the HTTP client, the session, translations, formatting and validation     |

What the applications share with Keycloak's pages, the language and the account actions, is the module
`@festival/shared/keycloak`; only its index is exported.

Each application forwards `/api/*`, `/oauth2/*`, `/login/oauth2/*` and `/logout` to the back-end at
`BACKEND_INTERNAL_URL` (`next.config.base.mjs`), so every call is same-origin and carries the session and CSRF cookies.

## Text

Every label and every error is a key of the back-end's catalog, formatted with `use-intl`
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

Each reads `BACKEND_INTERNAL_URL` from its `.env.development`.

> [!IMPORTANT]
>
> The panel runs on `127.0.0.1`, not `localhost`. Cookies are scoped to the host, not the port, so this keeps the
> page's and the panel's sessions apart, and Keycloak's `festival-admin` client accepts only that address as a
> redirect.

## Checks

```bash
pnpm format
pnpm lint
pnpm typecheck
pnpm test
pnpm check:translations   # every key in both languages
```

## Production

Each image is built for one application (`APP=page` or `APP=admin`) with `BACKEND_INTERNAL_URL`, the back-end's address
inside the cluster.

## Security headers

Every response carries `Strict-Transport-Security`, `X-Content-Type-Options`, `X-Frame-Options` and `Referrer-Policy`
(`next.config.base.mjs`). Each page also carries a `Content-Security-Policy` with a nonce of its own, set by the
application's `proxy.ts` (`packages/shared/src/security`): `_document` puts the nonce on the scripts Next.js writes and
on the `<style>` elements of styled-components, which reads it in the browser from the `csp-nonce` meta tag. With
`'strict-dynamic'`, a script runs only if it carries the nonce or is loaded by one that does, as Next.js's chunks are,
and the browser ignores `'self'` for scripts. No other inline script or style runs, and no `style` attribute applies:

- a component styles itself by a class, never by the `style` prop;
- a picture goes through `components/atoms/Image.tsx` in the page, which lays it out as `next/image` does but by a
  class;
- the page's entry animation is a CSS animation, and only its exit is left to Motion;
- each application's `pages/_error.tsx` draws Next.js's error page by classes, with its text from the catalog, or in
  English when the catalog cannot be loaded.

The fonts come from `next/font`, which downloads them when the application is built and serves them from its own
origin.

In production the policy has no `'unsafe-inline'`, `'unsafe-eval'` or `'unsafe-hashes'`. The development server adds
two, as Next.js's own guide to the Content-Security-Policy does: `'unsafe-eval'` to scripts, for React's debugging, and
`'unsafe-inline'` to styles, for the error overlay and the styles Next.js inserts as a module changes, neither of which
carries the nonce. Since a nonce makes a browser ignore `'unsafe-inline'`, the development policy drops the nonce
from styles.
