import { useTranslations } from "use-intl";
import { useLanguageSwitch } from "@festival/shared/i18n/IntlSetup";

export default function LanguageSwitcher({ className = "" }: Readonly<{ className?: string }>) {
  const t = useTranslations("common.language");
  const { current, languages, switchTo } = useLanguageSwitch();
  return (
    <div role="group" aria-label={t("label")} className={`flex gap-1 ${className}`}>
      {languages.map((language) => (
        <button
          key={language}
          type="button"
          lang={language}
          aria-pressed={language === current}
          aria-label={t(`name.${language}`)}
          onClick={() => switchTo(language)}
          className={`px-2 py-1 rounded-md text-sm text-white ${language === current ? "bg-indigo-700 font-bold" : "hover:bg-indigo-500"}`}
        >
          {t(`short.${language}`)}
        </button>
      ))}
    </div>
  );
}
