/** The request header that carries a page's nonce from the proxy to `_document`. */
export const NONCE_HEADER = "x-nonce";

/**
 * The Content-Security-Policy of a page, whose scripts run only from this origin or with its nonce, as the one Next.js
 * writes inline does.
 *
 * Every call goes to this origin, which forwards it to the back-end, and a sign-in or a sign-out is a navigation, not a
 * request, so connections stay on 'self'. Images come from the media storage and the profile picture from the identity
 * provider, both on other hosts, hence any https: image; fonts come from Google Fonts. Styles are inserted at run time
 * by styled-components and React.
 *
 * @param nonce - the page's nonce, new for every response
 * @param development - whether the development server runs, which also needs 'unsafe-eval' for React's debugging and
 *   serves the media storage over plain HTTP
 * @returns the value of the `Content-Security-Policy` header
 */
export function contentSecurityPolicy(nonce: string, development: boolean): string {
  return [
    "default-src 'self'",
    `script-src 'self' 'nonce-${nonce}'${development ? " 'unsafe-eval'" : ""}`,
    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",
    `img-src 'self' data: blob: https:${development ? " http:" : ""}`,
    "font-src 'self' data: https://fonts.gstatic.com",
    "connect-src 'self'",
    "object-src 'none'",
    "base-uri 'self'",
    "form-action 'self'",
    "frame-ancestors 'none'",
  ].join("; ");
}
