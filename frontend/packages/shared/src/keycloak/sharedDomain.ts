/**
 * The domain a cookie shared with Keycloak lives under: the parent of this host, such as `vertyll.dev` for
 * `festival.vertyll.dev`. `localhost` and an IP address have none, as every port there already shares cookies.
 */
export function sharedCookieDomain(hostname: string): string | null {
  const labels = hostname.split(".");
  const isAddress = labels.every((label) => /^\d+$/.test(label));
  return labels.length > 2 && !isAddress ? labels.slice(1).join(".") : null;
}
