/** The request header that carries a page's nonce from the proxy to `_document`. */
export const NONCE_HEADER = "x-nonce";

/**
 * The Content-Security-Policy of a page, whose scripts carry its nonce, as every script Next.js writes into it does, or
 * are loaded by one that does ('strict-dynamic'), and whose styles come from this origin or carry the nonce, as the
 * `<style>` elements of styled-components and of Next.js's own navigation do. No `style` attribute applies, so a
 * component styles itself by a class; a style set from a script, through the element's `style` property, is not an
 * attribute and still applies. Only the development server allows inline styles.
 *
 * Every call goes to this origin, which forwards it to the back-end, and a sign-in or a sign-out is a navigation, not a
 * request, so connections stay on 'self'. Images come from the media storage and the profile picture from the identity
 * provider, both on other hosts, hence any https: image. Fonts are downloaded when the application is built, by
 * `next/font`, and served from this origin.
 *
 * @param nonce - the page's nonce, new for every response
 * @param development - whether the development server runs, which also needs 'unsafe-eval' for React's debugging,
 *   inline styles without the nonce for its error overlay and the styles it adds as a module changes, and serves the
 *   media storage over plain HTTP
 * @returns the value of the `Content-Security-Policy` header
 */
export function contentSecurityPolicy(nonce: string, development: boolean): string {
  const nonceSource = `'nonce-${nonce}'`;
  return [
    "default-src 'self'",
    `script-src 'self' ${nonceSource} 'strict-dynamic'${development ? " 'unsafe-eval'" : ""}`,
    `style-src 'self' ${development ? "'unsafe-inline'" : nonceSource}`,
    `img-src 'self' data: blob: https:${development ? " http:" : ""}`,
    "font-src 'self'",
    "connect-src 'self'",
    "object-src 'none'",
    "base-uri 'self'",
    "form-action 'self'",
    "frame-ancestors 'none'",
  ].join("; ");
}
