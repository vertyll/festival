import { useTranslations } from "use-intl";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import LoadFailed from "@/components/atoms/LoadFailed";
import Spinner from "@/components/atoms/Spinner";
import Layout from "@/components/templates/Layout";
import { admin } from "@/lib/api";
import TranslationList from "@/components/organisms/TranslationList";

export default function TranslationsPage() {
  const t = useTranslations("admin.translations");
  const translations = useLoader(admin.translations.list);
  return (
    <Layout title={t("title")}>
      <LoadState state={translations.state} loading={<Spinner />} failed={<LoadFailed />}>
        {(data) => <TranslationList translations={data} reload={translations.reload} />}
      </LoadState>
    </Layout>
  );
}
