import { useTranslations } from "use-intl";

export default function LoadFailed() {
  const t = useTranslations("common");
  return <p>{t("loadFailed")}</p>;
}
