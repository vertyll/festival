import { useTranslations } from "use-intl";
import { useCookieConsent } from "@festival/shared/react/useCookieConsent";

export default function CookieBanner() {
  const t = useTranslations("common.cookies");
  const { consentMissing, accept } = useCookieConsent();

  if (!consentMissing) {
    return null;
  }
  return (
    <div className="fixed bottom-0 left-0 right-0 bg-white p-4 shadow-md z-50">
      <div className="container mx-auto flex flex-col sm:flex-row justify-between items-center">
        <p className="text-sm text-gray-700 mb-2 sm:mb-0">{t("text")}</p>
        <button
          type="button"
          onClick={accept}
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition-colors"
        >
          {t("accept")}
        </button>
      </div>
    </div>
  );
}
