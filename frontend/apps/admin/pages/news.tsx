import { useTranslations } from "use-intl";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import ResourceListPage from "@/components/templates/ResourceListPage";
import { admin } from "@/lib/api";

export default function NewsListPage() {
  const t = useTranslations("admin.news");
  const localized = useLocalized();
  return (
    <ResourceListPage
      title={t("title")}
      basePath="/news"
      addLabel={t("add")}
      nameHeader={t("name")}
      label={(item) => localized(item.name)}
      api={admin.news}
    />
  );
}
