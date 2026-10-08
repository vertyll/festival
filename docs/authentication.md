# Authentication

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
  access within minutes. Every account gets `USER`; only `ADMIN` opens the admin panel. A client without a browser calls
  the same API with `Authorization: Bearer` and its own Keycloak token: the same checks apply and no CSRF token is
  needed, since no cookie is involved.
- **State**: the back-end is stateless: every request is authorized by the JWT alone, so any instance can serve it. The
  only state is the browser session holding the tokens, and it lives in Redis, outside the application.
- **Token lifecycle**: access tokens live five minutes; every refresh returns a new refresh token and invalidates the
  old one, and concurrent requests of one session share a single refresh, across replicas too (a lock in Redis). A
  refresh Keycloak refuses ends the session, so a blocked account or a revoked role stops working within minutes.
  Signing out also ends the Keycloak session.
- **Accounts**: there is no local copy of a person; orders, addresses and wishlists are keyed by the Keycloak
  identifier.
