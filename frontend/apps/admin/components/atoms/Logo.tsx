import Link from "next/link";
import { useTranslations } from "use-intl";
import { Icon } from "./icons";

export default function Logo() {
  const t = useTranslations("admin");
  return (
    <Link href="/" className="flex gap-2 text-white">
      <Icon name="shop" />
      <span>{t("logo")}</span>
    </Link>
  );
}
