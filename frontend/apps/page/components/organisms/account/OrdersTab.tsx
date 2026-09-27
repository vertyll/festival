import { useTranslations } from "use-intl";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import LoadFailed from "@/components/atoms/LoadFailed";
import OrderSummary from "@/components/organisms/OrderSummary";
import { account } from "@/lib/api";
import AccountSpinner from "./AccountSpinner";
import { TabContent } from "./TabContent";

export default function OrdersTab() {
  const t = useTranslations("page.account");
  const orders = useLoader(account.orders);
  return (
    <LoadState state={orders.state} loading={<AccountSpinner />} failed={<LoadFailed />}>
      {(data) => (
        <TabContent>
          {data.length === 0 ? (
            <p>{t("noOrders")}</p>
          ) : (
            data.map((order) => <OrderSummary key={order.id} order={order} />)
          )}
        </TabContent>
      )}
    </LoadState>
  );
}
