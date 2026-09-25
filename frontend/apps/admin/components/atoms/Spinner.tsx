import { useTranslations } from "use-intl";

export default function Spinner() {
  const t = useTranslations("common");
  return (
    <div className="py-4 flex justify-center" role="status" aria-label={t("loading")}>
      <div className="spinner" />
    </div>
  );
}
