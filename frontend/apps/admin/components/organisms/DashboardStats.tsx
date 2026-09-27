import { useState } from "react";
import { useFormatter, useTranslations } from "use-intl";
import type { Order } from "@festival/shared/api/types";
import { recentOrders, revenue } from "@/lib/orders";
import StatTile from "@/components/molecules/StatTile";

const PERIODS = [
  { key: "today", days: 1 },
  { key: "week", days: 7 },
  { key: "month", days: 30 },
] as const;

export default function DashboardStats({ orders }: Readonly<{ orders: readonly Order[] }>) {
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
          <StatTile
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
          <StatTile
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
