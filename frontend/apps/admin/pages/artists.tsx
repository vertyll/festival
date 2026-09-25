import { useTranslations } from "use-intl";
import ResourceListPage from "@/components/templates/ResourceListPage";
import { admin } from "@/lib/api";

export default function ArtistListPage() {
  const t = useTranslations("admin.artists");
  return (
    <ResourceListPage
      title={t("title")}
      basePath="/artists"
      addLabel={t("add")}
      nameHeader={t("name")}
      label={(item) => item.name}
      api={admin.artists}
    />
  );
}
