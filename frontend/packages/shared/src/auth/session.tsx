import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { browserHttp } from "../api/http";
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

export function SessionProvider({ children }: Readonly<{ children: ReactNode }>) {
  const [session, setSession] = useState<SessionState>({ status: "loading" });

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

export function signIn(client: LoginClient): void {
  // eslint-disable-next-line @next/next/no-location-assign-relative-destination
  window.location.assign(`/oauth2/authorization/${client}`);
}

/**
 * A page Keycloak shows to a signed-in person before returning: changing the password, setting up the second factor, or
 * removing a credential named by its id, which Keycloak asks to confirm.
 */
export type AccountAction = "UPDATE_PASSWORD" | "CONFIGURE_TOTP" | `delete_credential:${string}`;

export function startAccountAction(client: LoginClient, action: AccountAction): void {
  // eslint-disable-next-line @next/next/no-location-assign-relative-destination
  window.location.assign(`/oauth2/authorization/${client}?kc_action=${action}`);
}

export async function signOut(): Promise<void> {
  const { logoutUrl } = await browserHttp.post<{ logoutUrl: string }>("/logout", {});
  window.location.assign(logoutUrl);
}
