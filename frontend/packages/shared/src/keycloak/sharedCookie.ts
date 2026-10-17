import { getCookie, setCookie } from "../browser/cookies";
import { sharedCookieDomain } from "./sharedDomain";

const ONE_YEAR_DAYS = 365;

/** Reads a cookie shared with Keycloak's pages. */
export function readShared(name: string): string | null {
  return getCookie(name);
}

/** Writes a cookie shared with Keycloak's pages, under the domain both live under, for a year. */
export function writeShared(name: string, value: string): void {
  setCookie(name, value, ONE_YEAR_DAYS, { domain: sharedCookieDomain(window.location.hostname) });
}
