import { useTranslations } from "use-intl";
import SponsorForm from "@/components/organisms/forms/SponsorForm";
import Layout from "@/components/templates/Layout";

export default function NewSponsorPage() {
  const t = useTranslations("admin.sponsors");
  return (
    <Layout title={t("newTitle")}>
      <SponsorForm sponsor={null} />
    </Layout>
  );
}
