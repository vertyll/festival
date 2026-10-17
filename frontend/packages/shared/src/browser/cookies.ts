export interface CookieScope {
  /** The domain the cookie lives under, shared with the hosts below it; without one it stays on this host. */
  domain?: string | null;
}

export function setCookie(name: string, value: string, days: number, scope: CookieScope = {}): void {
  const expires = new Date(Date.now() + days * 24 * 60 * 60 * 1000).toUTCString();
  const domain = scope.domain ? `; domain=${scope.domain}` : "";
  const secure = window.location.protocol === "https:" ? "; Secure" : "";
  document.cookie = `${name}=${encodeURIComponent(value)}; expires=${expires}; path=/; SameSite=Lax${domain}${secure}`;
}

export function getCookie(name: string): string | null {
  const prefix = `${name}=`;
  const cookie = document.cookie.split("; ").find((entry) => entry.startsWith(prefix));
  return cookie ? decodeURIComponent(cookie.slice(prefix.length)) : null;
}
