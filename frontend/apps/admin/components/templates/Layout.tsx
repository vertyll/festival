import Head from "next/head";
import { useRouter } from "next/router";
import { useState, type ReactNode } from "react";
import { useTranslations } from "use-intl";
import { signIn, signOut, useSession } from "@festival/shared/auth/session";
import Button from "../atoms/Button";
import Logo from "../atoms/Logo";
import Spinner from "../atoms/Spinner";
import { Icon } from "../atoms/icons";
import LanguageSwitcher from "../molecules/LanguageSwitcher";
import CookieBanner from "../organisms/CookieBanner";
import Sidebar from "../organisms/Sidebar";
import Topbar from "../organisms/Topbar";

function FullScreen({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <div className="bg-indigo-600 w-screen h-screen flex items-center justify-center">
      <LanguageSwitcher className="absolute top-4 right-4" />
      <div className="text-center text-white">{children}</div>
      <CookieBanner />
    </div>
  );
}

export default function Layout({ title, children }: Readonly<{ title: string; children: ReactNode }>) {
  const t = useTranslations("admin.layout");
  const { session } = useSession();
  const { query } = useRouter();
  const [showNav, setShowNav] = useState(false);
  const bold = (chunks: ReactNode) => <b>{chunks}</b>;

  let content: ReactNode;
  if (session.status === "loading") {
    content = (
      <FullScreen>
        <Spinner />
      </FullScreen>
    );
  } else if (session.status === "error") {
    content = (
      <FullScreen>
        <p>{t("sessionFailed")}</p>
      </FullScreen>
    );
  } else if (session.status === "anonymous") {
    content = (
      <FullScreen>
        {query.loginError !== undefined && <p className="mb-4">{t("loginError")}</p>}
        <Button variant="login" className="text-black" onClick={() => signIn("admin")}>
          {t.rich("googleLogin", { b: bold })}
        </Button>
      </FullScreen>
    );
  } else if (!session.user.administrator) {
    content = (
      <FullScreen>
        <p className="mb-4">{t.rich("notAdministrator", { b: bold, email: session.user.email })}</p>
        <Button variant="login" className="text-black" onClick={() => void signOut("/")}>
          {t("logout")}
        </Button>
      </FullScreen>
    );
  } else {
    content = (
      <div className="bg-indigo-600 min-h-screen">
        <div className="md:hidden flex items-center p-4">
          <button type="button" aria-label={t("menu")} onClick={() => setShowNav(!showNav)} className="text-white">
            <Icon name="menu" />
          </button>
          <div className="flex grow justify-center">
            <Logo />
          </div>
          <LanguageSwitcher />
        </div>
        <div className="flex">
          <Sidebar show={showNav} />
          <div className="bg-white grow">
            <Topbar user={session.user} />
            <main className="bg-white p-2 grow min-h-screen">
              <h1>{title}</h1>
              {children}
            </main>
          </div>
        </div>
      </div>
    );
  }

  return (
    <>
      <Head>
        <title>{t("title", { page: title })}</title>
      </Head>
      {content}
    </>
  );
}
