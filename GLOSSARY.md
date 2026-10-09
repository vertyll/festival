# Glossary

Every term the documentation uses without defining it on the spot, and where it is explained. The specifications behind
them are in [STANDARDS.md](STANDARDS.md).

| Term | Meaning | Explained in |
|---|---|---|
| Access token | Short-lived JWT (five minutes) that authorizes one request. | [Authentication: Every API request is authorized by a token](docs/authentication.md#every-api-request-is-authorized-by-a-token) |
| Artist, stage | The lineup: who performs and where. | [festival — back-end: Layout](backend/README.md#layout) |
| Attribute | A named list of option values, such as sizes, a product's options are chosen from. | [festival — back-end: Layout](backend/README.md#layout) |
| Audience | The `aud` claim naming whom a token is for; a token for anyone else is refused. | [Authentication: Every API request is authorized by a token](docs/authentication.md#every-api-request-is-authorized-by-a-token) |
| Authorization code flow | Sign-in by redirecting to Keycloak and exchanging the code it returns, on the server. | [Authentication: Signing in](docs/authentication.md#signing-in) |
| BFF (backend for frontend) | The back-end signs the user in and keeps the tokens; the browser holds only a session cookie. | [Authentication](docs/authentication.md) |
| Cart | The products a visitor is about to order; it lives in the browser until an order is placed. | [festival — back-end: Shop](backend/README.md#shop) |
| Catalog | Every label and error key of both applications, in Polish and English, owned by the back-end. | [Architecture: Errors and translations](docs/architecture.md#errors-and-translations) |
| Checkout switch | `FESTIVAL_CHECKOUT_ENABLED`, which stops orders without hiding the shop. | [festival — back-end: Shop](backend/README.md#shop) |
| CSRF token | The `XSRF-TOKEN` cookie value every unsafe request repeats in `X-XSRF-TOKEN`. | [Authentication: CSRF](docs/authentication.md#csrf) |
| Keycloak realm | The Keycloak tenant holding this application's users, roles and clients. | [Authentication](docs/authentication.md) |
| Login client | `festival-page` or `festival-admin`: one Keycloak client per application, each with its own landing path. | [Authentication: Two clients](docs/authentication.md#two-clients) |
| Message key | A key of the catalog the back-end sends instead of a sentence; the front-end renders it. | [Architecture: Errors and translations](docs/architecture.md#errors-and-translations) |
| News, sponsor | The festival's content, edited in the admin panel. | [festival — back-end: Layout](backend/README.md#layout) |
| Object storage | S3-compatible storage for images (Garage locally); images are served from it directly. | [festival — back-end: Media](backend/README.md#media) |
| Order | A placed cart, keyed by the customer's Keycloak identifier. | [festival — back-end: Shop](backend/README.md#shop) |
| Override | An administrator's text for a key, set one at a time or by importing a spreadsheet. | [festival — back-end: Translations](backend/README.md#translations) |
| PKCE | A one-time secret binding the returned code to the browser that started the sign-in. | [Authentication: Signing in](docs/authentication.md#signing-in) |
| Problem document | The JSON body of every refusal, carrying a message key and its arguments. | [Architecture: Errors and translations](docs/architecture.md#errors-and-translations) |
| Proxy | Each Next.js application forwarding `/api`, `/oauth2`, `/login/oauth2` and `/logout` to the back-end. | [Architecture: Three applications, one back-end](docs/architecture.md#three-applications-one-back-end) |
| Refresh token | Long-lived token traded for a new access token; Keycloak rotates it on every use. | [Authentication: Sessions and refreshing](docs/authentication.md#sessions-and-refreshing) |
| Refresh token rotation | Every refresh invalidates the refresh token it used; replaying a spent one ends the session. | [Authentication: Sessions and refreshing](docs/authentication.md#sessions-and-refreshing) |
| Session | `FESTIVAL_SESSION`, kept in Redis for ten hours, holding the tokens. | [Authentication: Sessions and refreshing](docs/authentication.md#sessions-and-refreshing) |
| Shop settings | Shipping price and stock visibility, changed by an admin. | [festival — back-end: Shop](backend/README.md#shop) |
| Sign-out | Ends the local session and the Keycloak session. | [Authentication: Signing out](docs/authentication.md#signing-out) |
| Single-flight refresh | One refresh per refresh token, shared by every request of the session that needs it at once. | [Authentication: Sessions and refreshing](docs/authentication.md#sessions-and-refreshing) |
| Stock reservation | Taking stock line by line when an order is placed, released again if any line fails. | [festival — back-end: Shop](backend/README.md#shop) |
| Wishlist, address | A signed-in customer's saved products and delivery addresses. | [festival — back-end: Accounts](backend/README.md#accounts) |
