import { useState } from "react";
import { useTranslations } from "use-intl";
import type { SessionUser } from "@festival/shared/api/types";
import { signOut } from "@festival/shared/auth/session";
import { Icon } from "../atoms/icons";
import LanguageSwitcher from "../molecules/LanguageSwitcher";

export default function Topbar({ user }: Readonly<{ user: SessionUser }>) {
  const t = useTranslations("admin.nav");
  const [showMenu, setShowMenu] = useState(false);
  return (
    <div className="text-black md:flex justify-end items-center gap-4 p-2 bg-indigo-300 hidden">
      <LanguageSwitcher />
      <div className="relative" onMouseEnter={() => setShowMenu(true)} onMouseLeave={() => setShowMenu(false)}>
        <div className="flex items-center text-white gap-2 p-2 rounded-md uppercase text-sm">
          {user.picture && (
            // eslint-disable-next-line @next/next/no-img-element -- awatar z Google, poza optymalizacją obrazów
            <img src={user.picture} alt="" className="w-10 h-10 rounded-full border-2" referrerPolicy="no-referrer" />
          )}
          <span className="py-1 px-2">
            <b>{user.name ?? user.email}</b>
          </span>
          <Icon name="chevronDown" className="w-4 h-4" />
        </div>
        {showMenu && (
          <div className="flex absolute right-0 py-2 w-48 bg-white rounded-md shadow-xl z-10">
            <button
              type="button"
              className="flex gap-2 items-center p-2 w-full text-sm rounded-md text-gray-700 hover:bg-indigo-300 hover:text-white transition duration-300"
              onClick={() => void signOut("/")}
            >
              <Icon name="logout" />
              {t("logout")}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
