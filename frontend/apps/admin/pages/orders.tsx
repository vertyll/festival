import type { ReactNode } from "react";
import { useFormatter, useTranslations } from "use-intl";
import type { Order } from "@festival/shared/api/types";
import { useDescribeSelection, useLocalized } from "@festival/shared/i18n/IntlSetup";
import { useLoader } from "@festival/shared/react/useLoader";
import DataTable, { type Column } from "@/components/organisms/DataTable";
import Layout from "@/components/templates/Layout";
import { admin } from "@/lib/api";

export default function OrdersPage() {
  const t = useTranslations("admin.orders");
  const tCommon = useTranslations("common");
  const format = useFormatter();
  const localized = useLocalized();
  const describeSelection = useDescribeSelection();
  const orders = useLoader(admin.orders.list);
  const bold = (chunks: ReactNode) => <b>{chunks}</b>;
  const money = (order: Order, amount: number) =>
    format.number(amount, { style: "currency", currency: order.currency });

  const columns: readonly Column<Order>[] = [
    {
      header: t("date"),
      render: (order) => format.dateTime(new Date(order.createdAt), { dateStyle: "medium", timeStyle: "short" }),
    },
    {
      header: t("status"),
      render: (order) => (
        <span className={order.status === "PAID" ? "text-green-600" : "text-red-600"}>
          {tCommon(`orderStatus.${order.status}`)}
        </span>
      ),
    },
    {
      header: t("shipping"),
      render: ({ shipping }) => (
        <div className="text-left">
          {t.rich("recipient", { b: bold, name: shipping.name })} <br />
          {t.rich("email", { b: bold, email: shipping.email })} <br />
          {t.rich("address", { b: bold, address: shipping.streetAddress })} <br />
          {t.rich("city", { b: bold, postalCode: shipping.postalCode, city: shipping.city })} <br />
          {t.rich("country", { b: bold, country: shipping.country })}
        </div>
      ),
    },
    {
      header: t("products"),
      render: (order) => (
        <div className="text-left">
          {order.lines.map((line) => (
            <div key={`${line.productId}:${line.valueCodes.join("|")}`}>
              {tCommon("orderLine", {
                quantity: line.quantity,
                product: localized(line.productName),
                total: money(order, line.lineTotal),
              })}
              {line.selection.length > 0 && ` ${tCommon("inParentheses", { text: describeSelection(line.selection) })}`}
            </div>
          ))}
        </div>
      ),
    },
    { header: t("total"), render: (order) => money(order, order.total) },
  ];

  return (
    <Layout title={t("title")}>
      <div className="overflow-x-auto">
        <DataTable state={orders.state} itemKey={(order) => order.id} columns={columns} emptyMessage={t("empty")} />
      </div>
    </Layout>
  );
}
