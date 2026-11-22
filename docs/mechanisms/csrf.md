# CSRF

Why a forged request from another site cannot act with the user's session cookie.

The session is a cookie, so every unsafe request needs the CSRF token: the back-end sets it in the `__Host-XSRF-TOKEN`
cookie and the shared HTTP client sends it back in `X-XSRF-TOKEN`. A request carrying `Authorization` is exempt, because
no cookie is involved.

The cookie is readable by scripts, so the client can repeat it, and is `Secure`, `SameSite=Lax`, `Path=/` and without
`Domain` wherever `server.servlet.session.cookie.secure` is on, as it takes its `Secure` from the session cookie. The
`__Host-` prefix makes the browser accept the cookie only from this host, so a sibling host under the same parent domain
cannot plant a token of its own. The `local` profile, served over plain HTTP, turns that setting off and names it
`XSRF-TOKEN`, as the prefix requires `Secure`; the client reads `__Host-XSRF-TOKEN` over HTTPS and `XSRF-TOKEN` over
plain HTTP (`csrfCookieName`).
