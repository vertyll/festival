import { useTranslations } from "use-intl";
import ArtistForm from "@/components/organisms/forms/ArtistForm";
import Layout from "@/components/templates/Layout";

export default function NewArtistPage() {
  const t = useTranslations("admin.artists");
  return (
    <Layout title={t("newTitle")}>
      <ArtistForm artist={null} />
    </Layout>
  );
}
