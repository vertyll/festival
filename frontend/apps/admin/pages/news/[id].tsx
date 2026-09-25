import { useTranslations } from "use-intl";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import NewsForm from "@/components/organisms/forms/NewsForm";
import ResourceEditorPage from "@/components/templates/ResourceEditorPage";
import { admin } from "@/lib/api";

export default function EditNewsPage() {
  const t = useTranslations("admin.news");
  const localized = useLocalized();
  return (
    <ResourceEditorPage title={(news) => t("editTitle", { name: localized(news.name) })} load={admin.news.get}>
      {(news) => <NewsForm news={news} />}
    </ResourceEditorPage>
  );
}
