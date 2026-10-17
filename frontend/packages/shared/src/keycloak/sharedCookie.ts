import { getCookie, setCookie } from "../browser/cookies";

const ONE_YEAR_DAYS = 365;

/**
 * The domain a cookie shared with Keycloak lives under: the parent of this host, such as `vertyll.dev` for
 * `festival.vertyll.dev`. `localhost` and an IP address have none, as every port there already shares cookies.
 */
export function sharedCookieDomain(hostname: string): string | null {
  const labels = hostname.split(".");
  const isAddress = labels.every((label) => /^\d+$/.test(label));
  return labels.length > 2 && !isAddress ? labels.slice(1).join(".") : null;
}

/** Reads a cookie shared with Keycloak's pages. */
export function readShared(name: string): string | null {
  return getCookie(name);
}

/** Writes a cookie shared with Keycloak's pages, under the domain both live under, for a year. */
export function writeShared(name: string, value: string): void {
  setCookie(name, value, ONE_YEAR_DAYS, { domain: sharedCookieDomain(window.location.hostname) });
}
