import { useTranslations } from "use-intl";
import NewsForm from "@/components/organisms/forms/NewsForm";
import Layout from "@/components/templates/Layout";

export default function NewNewsPage() {
  const t = useTranslations("admin.news");
  return (
    <Layout title={t("newTitle")}>
      <NewsForm news={null} />
    </Layout>
  );
}
