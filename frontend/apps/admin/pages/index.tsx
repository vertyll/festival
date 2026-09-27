import { useTranslations } from "use-intl";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import LoadFailed from "@/components/atoms/LoadFailed";
import Spinner from "@/components/atoms/Spinner";
import Layout from "@/components/templates/Layout";
import { admin } from "@/lib/api";
import DashboardStats from "@/components/organisms/DashboardStats";

export default function DashboardPage() {
  const t = useTranslations("admin.dashboard");
  const orders = useLoader(admin.orders.list);
  return (
    <Layout title={t("title")}>
      <LoadState state={orders.state} loading={<Spinner />} failed={<LoadFailed />}>
        {(data) => <DashboardStats orders={data} />}
      </LoadState>
    </Layout>
  );
}
