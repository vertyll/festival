import Head from "next/head";
import { useState, type ReactNode } from "react";
import { useTranslations } from "use-intl";
import Logo from "../atoms/Logo";
import { Icon } from "../atoms/icons";
import LanguageSwitcher from "../molecules/LanguageSwitcher";
import Sidebar from "../organisms/Sidebar";
import Topbar from "../organisms/Topbar";
import { useAdminUser } from "./AdminGate";

export default function Layout({ title, children }: Readonly<{ title: string; children: ReactNode }>) {
  const t = useTranslations("admin.layout");
  const user = useAdminUser();
  const [showNav, setShowNav] = useState(false);

  return (
    <>
      <Head>
        <title>{t("title", { page: title })}</title>
      </Head>
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
            <Topbar user={user} />
            <main className="bg-white p-2 grow min-h-screen">
              <h1>{title}</h1>
              {children}
            </main>
          </div>
        </div>
      </div>
    </>
  );
}
