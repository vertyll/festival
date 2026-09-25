import styled from "styled-components";
import { useFormatter, useTranslations } from "use-intl";
import type { Order } from "@festival/shared/api/types";
import { useDescribeSelection, useLocalized } from "@festival/shared/i18n/IntlSetup";

const StyledOrder = styled.div`
  margin: 2px 0;
  padding: 10px 0;
  border-bottom: 1px solid var(--border-color-for-under);
  display: flex;
  gap: 50px;
  align-items: center;
  flex-wrap: wrap;
`;

const Muted = styled.div`
  font-size: 0.8rem;
  line-height: 1rem;
  margin-top: 5px;
  color: var(--gray-color);
`;

export default function OrderSummary({ order }: Readonly<{ order: Order }>) {
  const t = useTranslations();
  const format = useFormatter();
  const localized = useLocalized();
  const describeSelection = useDescribeSelection();
  const money = (amount: number) => format.number(amount, { style: "currency", currency: order.currency });
  const { shipping } = order;
  return (
    <StyledOrder>
      <div>
        {format.dateTime(new Date(order.createdAt), { dateStyle: "medium", timeStyle: "short" })}
        <Muted>{t(`common.orderStatus.${order.status}`)}</Muted>
        <Muted>
          {shipping.name}
          <br />
          {shipping.email}
          <br />
          {shipping.streetAddress}
          <br />
          {shipping.postalCode} {shipping.city}, {shipping.country}
        </Muted>
      </div>
      <div>
        {order.lines.map((line) => (
          <p key={`${line.productId}:${line.valueCodes.join("|")}`}>
            {t("common.orderLine", {
              quantity: line.quantity,
              product: localized(line.productName),
              total: money(line.lineTotal),
            })}
            {line.selection.length > 0 && ` ${t("common.inParentheses", { text: describeSelection(line.selection) })}`}
          </p>
        ))}
        <p>
          <b>{t("common.orderTotal", { total: money(order.total) })}</b>
        </p>
      </div>
    </StyledOrder>
  );
}
