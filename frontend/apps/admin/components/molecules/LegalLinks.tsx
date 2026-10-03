import Link from "next/link";
import { useTranslations } from "use-intl";

export default function LegalLinks({ className }: Readonly<{ className?: string }>) {
  const t = useTranslations("legal");
  return (
    <nav className={`flex gap-4 text-sm ${className ?? ""}`} aria-label={t("navigation")}>
      <Link href="/terms" className="hover:underline">
        {t("termsOfUse.title")}
      </Link>
      <Link href="/privacy" className="hover:underline">
        {t("privacyPolicy.title")}
      </Link>
    </nav>
  );
}
