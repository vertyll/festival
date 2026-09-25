import { useState } from "react";
import { useFormatter, useTranslations } from "use-intl";
import type { Order } from "@festival/shared/api/types";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import LoadFailed from "@/components/atoms/LoadFailed";
import Spinner from "@/components/atoms/Spinner";
import Layout from "@/components/templates/Layout";
import { admin } from "@/lib/api";
import { recentOrders, revenue } from "@/lib/orders";

const PERIODS = [
  { key: "today", days: 1 },
  { key: "week", days: 7 },
  { key: "month", days: 30 },
] as const;

function Tile({ header, value, description }: Readonly<{ header: string; value: string; description: string }>) {
  return (
    <div className="tile">
      <h3 className="tile-header">{header}</h3>
      <div className="tile-number">{value}</div>
      <div className="tile-desc">{description}</div>
    </div>
  );
}

function Stats({ orders }: Readonly<{ orders: readonly Order[] }>) {
  const t = useTranslations("admin.dashboard");
  const format = useFormatter();
  const [now] = useState(() => Date.now());
  const periods = PERIODS.map((period) => ({ ...period, orders: recentOrders(orders, period.days, now) }));
  const currency = orders[0]?.currency;
  return (
    <div className="my-2 mx-10">
      <h2 className="title-title">{t("orders")}</h2>
      <div className="tiles-grid">
        {periods.map((period) => (
          <Tile
            key={period.key}
            header={t(`periods.${period.key}`)}
            value={format.number(period.orders.length)}
            description={t("lastDays", { days: period.days })}
          />
        ))}
      </div>
      <h2 className="title-title">{t("revenue")}</h2>
      <div className="tiles-grid">
        {periods.map((period) => (
          <Tile
            key={period.key}
            header={t(`periods.${period.key}`)}
            value={currency ? format.number(revenue(period.orders), { style: "currency", currency }) : format.number(0)}
            description={t("orderCount", { count: period.orders.length })}
          />
        ))}
      </div>
    </div>
  );
}

export default function DashboardPage() {
  const t = useTranslations("admin.dashboard");
  const orders = useLoader(admin.orders.list);
  return (
    <Layout title={t("title")}>
      <LoadState state={orders.state} loading={<Spinner />} failed={<LoadFailed />}>
        {(data) => <Stats orders={data} />}
      </LoadState>
    </Layout>
  );
}
