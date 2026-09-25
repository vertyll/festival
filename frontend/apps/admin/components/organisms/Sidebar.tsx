import Link from "next/link";
import { useRouter } from "next/router";
import { useTranslations } from "use-intl";
import { signOut } from "@festival/shared/auth/session";
import Logo from "../atoms/Logo";
import { Icon, type IconName } from "../atoms/icons";

const NAV_ITEMS = [
  { href: "/", label: "dashboard", icon: "home" },
  { href: "/orders", label: "orders", icon: "orders" },
  { href: "/products", label: "products", icon: "products" },
  { href: "/artists", label: "artists", icon: "artists" },
  { href: "/categories", label: "categories", icon: "categories" },
  { href: "/attributes", label: "attributes", icon: "attributes" },
  { href: "/stages", label: "stages", icon: "stages" },
  { href: "/news", label: "news", icon: "news" },
  { href: "/sponsors", label: "sponsors", icon: "sponsors" },
  { href: "/translations", label: "translations", icon: "translations" },
  { href: "/administrators", label: "administrators", icon: "administrators" },
  { href: "/settings", label: "settings", icon: "settings" },
] as const satisfies readonly { href: string; label: string; icon: IconName }[];

const LINK = "flex gap-2 p-2 rounded-md hover:bg-indigo-700 transition duration-300";
const ACTIVE_LINK = `${LINK} bg-indigo-700 text-white`;

function isActive(pathname: string, href: string): boolean {
  return href === "/" ? pathname === "/" : pathname === href || pathname.startsWith(`${href}/`);
}

export default function Sidebar({ show }: Readonly<{ show: boolean }>) {
  const t = useTranslations("admin.nav");
  const { pathname } = useRouter();
  return (
    <aside
      className={`${show ? "left-0" : "-left-full"} transition-all md:w-auto md:static top-0 text-white p-4 fixed w-full bg-indigo-600 h-full z-50`}
    >
      <div className="mb-10 p-2">
        <Logo />
      </div>
      <nav className="flex flex-col gap-2 md:w-64">
        {NAV_ITEMS.map((item) => (
          <Link key={item.href} href={item.href} className={isActive(pathname, item.href) ? ACTIVE_LINK : LINK}>
            <Icon name={item.icon} />
            {t(item.label)}
          </Link>
        ))}
        <button type="button" className={LINK} onClick={() => void signOut("/")}>
          <Icon name="logout" />
          {t("logout")}
        </button>
      </nav>
    </aside>
  );
}
