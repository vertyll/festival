/**
 * The cookie the back end keeps the CSRF token in. Over HTTPS it carries the `__Host-` prefix, which keeps a sibling
 * host from planting its own token; the `local` profile, served over plain HTTP, cannot set the `Secure` cookie the
 * prefix requires and uses the bare name.
 */
export function csrfCookieName(protocol: string): string {
  return protocol === "https:" ? "__Host-XSRF-TOKEN" : "XSRF-TOKEN";
}
