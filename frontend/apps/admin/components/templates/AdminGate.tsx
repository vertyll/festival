import Head from "next/head";
import { useRouter } from "next/router";
import { createContext, useContext, type ReactNode } from "react";
import { useTranslations } from "use-intl";
import type { SessionUser } from "@festival/shared/api/types";
import { signIn, signOut, useSession } from "@festival/shared/auth/session";
import Button from "../atoms/Button";
import Spinner from "../atoms/Spinner";
import LanguageSwitcher from "../molecules/LanguageSwitcher";
import LegalLinks from "../molecules/LegalLinks";
import CookieBanner from "../organisms/CookieBanner";

const AdminUserContext = createContext<SessionUser | null>(null);

export function useAdminUser(): SessionUser {
  const user = useContext(AdminUserContext);
  if (!user) {
    throw new Error("useAdminUser() must be used within <AdminGate>");
  }
  return user;
}

function FullScreen({ children }: Readonly<{ children: ReactNode }>) {
  const t = useTranslations("admin.layout");
  return (
    <>
      <Head>
        <title>{t("title", { page: t("signIn") })}</title>
      </Head>
      <div className="bg-indigo-600 w-screen h-screen flex items-center justify-center">
        <LanguageSwitcher className="absolute top-4 right-4" />
        <div className="text-center text-white">{children}</div>
        <LegalLinks className="absolute bottom-6 text-white" />
        <CookieBanner />
      </div>
    </>
  );
}

export default function AdminGate({ children }: Readonly<{ children: ReactNode }>) {
  const t = useTranslations("admin.layout");
  const { session } = useSession();
  const { query } = useRouter();
  const bold = (chunks: ReactNode) => <b>{chunks}</b>;

  if (session.status === "loading") {
    return (
      <FullScreen>
        <Spinner />
      </FullScreen>
    );
  }
  if (session.status === "error") {
    return (
      <FullScreen>
        <p>{t("sessionFailed")}</p>
      </FullScreen>
    );
  }
  if (session.status === "anonymous") {
    return (
      <FullScreen>
        {query.loginError !== undefined && <p className="mb-4">{t("loginError")}</p>}
        <Button variant="login" className="text-black" onClick={() => signIn("admin")}>
          {t("login")}
        </Button>
      </FullScreen>
    );
  }
  if (!session.user.administrator) {
    return (
      <FullScreen>
        <p className="mb-4">{t.rich("notAdministrator", { b: bold, email: session.user.email })}</p>
        <Button variant="login" className="text-black" onClick={() => void signOut()}>
          {t("logout")}
        </Button>
      </FullScreen>
    );
  }
  return <AdminUserContext.Provider value={session.user}>{children}</AdminUserContext.Provider>;
}
