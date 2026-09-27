import { useTranslations } from "use-intl";

export default function Spinner() {
  const t = useTranslations("common");
  return (
    <output className="py-4 flex justify-center" aria-label={t("loading")}>
      <div className="spinner" />
    </output>
  );
}
