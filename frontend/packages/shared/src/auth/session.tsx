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

/** Where the browser starts a sign-in with this client, through the back-end. */
export function authorizationPath(client: LoginClient): string {
  return `/oauth2/authorization/${client}`;
}

export function signIn(client: LoginClient): void {
  window.location.assign(authorizationPath(client));
}

export async function signOut(): Promise<void> {
  const { logoutUrl } = await browserHttp.post<{ logoutUrl: string }>("/logout", {});
  window.location.assign(logoutUrl);
}
