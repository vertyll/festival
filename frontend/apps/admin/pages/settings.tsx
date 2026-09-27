import { useTranslations } from "use-intl";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import LoadFailed from "@/components/atoms/LoadFailed";
import Spinner from "@/components/atoms/Spinner";
import Layout from "@/components/templates/Layout";
import { admin } from "@/lib/api";
import SettingsForm from "@/components/organisms/forms/SettingsForm";

export default function SettingsPage() {
  const t = useTranslations("admin.settings");
  const settings = useLoader(admin.settings.get);
  return (
    <Layout title={t("title")}>
      <LoadState state={settings.state} loading={<Spinner />} failed={<LoadFailed />}>
        {(data) => <SettingsForm settings={data} />}
      </LoadState>
    </Layout>
  );
}
