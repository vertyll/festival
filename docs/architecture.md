# Architecture

## Three applications, one back-end

| Application                                                | What it is                                                      |
|------------------------------------------------------------|-----------------------------------------------------------------|
| `frontend/apps/page` ([front-end](../frontend/README.md))  | the festival page and shop (Next.js)                            |
| `frontend/apps/admin` ([front-end](../frontend/README.md)) | the admin panel (Next.js)                                       |
| `backend` ([back-end](../backend/README.md))               | a Spring Boot API over MongoDB, Redis and S3-compatible storage |

The browser talks only to its Next.js application. Each application forwards `/api/*`, `/oauth2/*`,
`/login/oauth2/*` and `/logout` to the back-end (`frontend/next.config.base.mjs`), so the back-end's cookies are set on
the application's own address and every API call is same-origin.

## Errors and translations

The back-end never sends a sentence a person reads. Every refusal is an RFC 9457 problem document
(`application/problem+json`, `common/ApiExceptionHandler`):

| Field    | Holds                                                                                            |
|----------|--------------------------------------------------------------------------------------------------|
| `status` | the HTTP status                                                                                  |
| `code`   | a key of the translation catalogue; a refusal Spring raises itself gets `errors.status.{status}` |
| `args`   | the ICU arguments for that key                                                                   |
| `errors` | in a validation error, one `{ code, args }` per invalid field                                    |

The catalogue is the back-end's: it ships in `backend/src/main/resources/i18n` (`pl.json`, `en.json`, ICU
MessageFormat), an admin can override any message, and `GET /api/i18n/{language}` serves it.

The front-end renders it. Both applications load the catalogue for the reader's language and format messages with
`use-intl`. The shared HTTP client turns a failed call into an `ApiError`: `userMessage` is the problem's
`{ code, args }`, and `fieldErrors` the per-field messages a form shows next to its inputs; a response without a
problem document becomes `errors.status.{status}`. The same catalogue holds every label of both applications, so a new
error or a new label is a new key in both files, never a sentence in the code; `pnpm check:translations` fails when a
key is missing in one language.
