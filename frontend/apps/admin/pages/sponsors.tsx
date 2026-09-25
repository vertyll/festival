import { useTranslations } from "use-intl";
import ResourceListPage from "@/components/templates/ResourceListPage";
import { admin } from "@/lib/api";

export default function SponsorListPage() {
  const t = useTranslations("admin.sponsors");
  return (
    <ResourceListPage
      title={t("title")}
      basePath="/sponsors"
      addLabel={t("add")}
      nameHeader={t("name")}
      label={(item) => item.name}
      api={admin.sponsors}
    />
  );
}
