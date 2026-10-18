import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { browserHttp, whenUnauthorized } from "../api/http";
import { publicApi } from "../api/public";
import type { SessionUser } from "../api/types";

export type LoginClient = "page" | "admin";

export type SessionState =
  | { status: "loading" }
  | { status: "anonymous" }
  | { status: "authenticated"; user: SessionUser }
  | { status: "error"; error: unknown };

interface SessionContextValue {
  session: SessionState;
}

const SessionContext = createContext<SessionContextValue | null>(null);

async function loadSession(): Promise<SessionState> {
  try {
    const { user } = await publicApi(browserHttp).me();
    return user ? { status: "authenticated", user } : { status: "anonymous" };
  } catch (error) {
    return { status: "error", error };
  }
}

/**
 * Holds who is signed in to this application, signed in with `client`. A session that ends while a page is open sends
 * the reader to sign in again, and back to that page.
 */
export function SessionProvider({ client, children }: Readonly<{ client: LoginClient; children: ReactNode }>) {
  const [session, setSession] = useState<SessionState>({ status: "loading" });
  const signedIn = session.status === "authenticated";

  useEffect(() => (signedIn ? whenUnauthorized(() => signIn(client, thisPage())) : undefined), [client, signedIn]);

  useEffect(() => {
    let active = true;
    void loadSession().then((state) => {
      if (active) {
        setSession(state);
      }
    });
    return () => {
      active = false;
    };
  }, []);

  const value = useMemo(() => ({ session }), [session]);
  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession(): SessionContextValue {
  const value = useContext(SessionContext);
  if (!value) {
    throw new Error("useSession() must be used within <SessionProvider>");
  }
  return value;
}

/**
 * Where the browser starts a sign-in with this client, through the back-end, which returns to `returnTo`, a path on
 * this site, once the person is signed in, and asks Keycloak for one account page first when `action` names one.
 */
export function authorizationPath(client: LoginClient, returnTo: string, action?: string): string {
  const query = new URLSearchParams({ return_to: returnTo });
  if (action) {
    query.set("kc_action", action);
  }
  return `/oauth2/authorization/${client}?${query}`;
}

/** The page the reader is on, which a sign-in started here returns to. */
export function thisPage(): string {
  return window.location.pathname + window.location.search;
}

export function signIn(client: LoginClient, returnTo: string): void {
  window.location.assign(authorizationPath(client, returnTo));
}

export async function signOut(): Promise<void> {
  const { logoutUrl } = await browserHttp.post<{ logoutUrl: string }>("/logout", {});
  window.location.assign(logoutUrl);
}
