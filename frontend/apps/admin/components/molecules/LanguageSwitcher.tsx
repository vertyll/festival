import { useTranslations } from "use-intl";
import { useLanguageSwitch } from "@festival/shared/i18n/IntlSetup";

const TONES = {
  onDark: { current: "bg-indigo-700 text-white font-bold", other: "text-white hover:bg-indigo-500" },
  onLight: { current: "bg-indigo-700 text-white font-bold", other: "text-indigo-700 hover:bg-indigo-100" },
} as const;

export default function LanguageSwitcher({
  className = "",
  tone = "onDark",
}: Readonly<{ className?: string; tone?: keyof typeof TONES }>) {
  const t = useTranslations("common.language");
  const { current, languages, switchTo } = useLanguageSwitch();
  return (
    <fieldset aria-label={t("label")} className={`flex gap-1 border-0 p-0 m-0 min-w-0 ${className}`}>
      {languages.map((language) => (
        <button
          key={language}
          type="button"
          lang={language}
          aria-pressed={language === current}
          aria-label={t(`name.${language}`)}
          onClick={() => switchTo(language)}
          className={`px-2 py-1 rounded-md text-sm ${language === current ? TONES[tone].current : TONES[tone].other}`}
        >
          {t(`short.${language}`)}
        </button>
      ))}
    </fieldset>
  );
}
