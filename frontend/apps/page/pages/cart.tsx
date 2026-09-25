import Image from "next/image";
import Link from "next/link";
import { useEffect, useState, type ReactNode } from "react";
import styled from "styled-components";
import { useFormatter, useTranslations } from "use-intl";
import { ApiError } from "@festival/shared/api/http";
import type { Order, ProductCard } from "@festival/shared/api/types";
import { signIn, useSession } from "@festival/shared/auth/session";
import { useDescribeSelection, useLocalized, useMessage } from "@festival/shared/i18n/IntlSetup";
import { Alert } from "@/components/atoms/Alert";
import Button from "@/components/atoms/Button";
import DivCenter from "@/components/atoms/DivCenter";
import LoadFailed from "@/components/atoms/LoadFailed";
import LottieAnimation from "@/components/atoms/LottieAnimation";
import PageTitle from "@/components/atoms/PageTitle";
import Price from "@/components/atoms/Price";
import Spinner from "@/components/atoms/Spinner";
import Table from "@/components/atoms/Table";
import { IconCreditCart } from "@/components/atoms/icons";
import ShippingDetailsForm from "@/components/molecules/ShippingDetailsForm";
import Layout from "@/components/templates/Layout";
import { account, api } from "@/lib/api";
import { canIncrease, cart, cartItemKey, useCartItems, type CartItem } from "@/lib/cart";
import { shippingErrorsOf, useShippingForm } from "@/lib/shipping";
import { useShopSettings } from "@/lib/shopSettings";

const Wrapper = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: 50px;
  max-width: 1000px;
  width: 100%;
  margin-top: 50px;

  @media screen and (min-width: 768px) {
    grid-template-columns: 1.2fr 0.8fr;
  }
`;

const Title = styled.h1`
  margin: 0;
  font-weight: bold;
`;

const Box = styled.div`
  background-color: var(--main-white-smoke-color);
  border-radius: 20px;
  padding: 30px;
  box-shadow: var(--default-box-shadow);

  input {
    background-color: var(--light-color);
  }
`;

const Centered = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
`;

const Thumbnail = styled.div`
  width: 70px;
  height: 70px;
  position: relative;

  @media screen and (min-width: 768px) {
    width: 100px;
  }
`;

const QuantityLabel = styled.span`
  padding: 0 15px;
  display: block;

  @media screen and (min-width: 768px) {
    display: inline-block;
    padding: 0 10px;
  }
`;

const Options = styled.div`
  color: var(--gray-color);
  font-weight: bold;
  font-size: 0.9em;
`;

const ShopLink = styled(Link)`
  color: var(--dark-text-color);
  font-weight: bold;
`;

interface CartLine {
  item: CartItem;
  product: ProductCard;
}

function useCartLines(items: readonly CartItem[]): CartLine[] | null {
  const [products, setProducts] = useState<ReadonlyMap<string, ProductCard> | null>(null);
  const ids = [...new Set(items.map((item) => item.productId))].sort((a, b) => a.localeCompare(b)).join(",");

  useEffect(() => {
    if (ids === "") {
      return undefined;
    }
    let active = true;
    void api.products({ ids: ids.split(",") }).then((found) => {
      if (active) {
        const byId = new Map(found.map((product) => [product.id, product]));
        cart.retain(new Set(byId.keys()));
        setProducts(byId);
      }
    });
    return () => {
      active = false;
    };
  }, [ids]);

  if (items.length === 0) {
    return [];
  }
  if (products === null) {
    return null;
  }
  return items.flatMap((item) => {
    const product = products.get(item.productId);
    return product ? [{ item, product }] : [];
  });
}

export default function CartPage() {
  const t = useTranslations("page.cart");
  const format = useFormatter();
  const localized = useLocalized();
  const describeMessage = useMessage();
  const describeSelection = useDescribeSelection();
  const { session } = useSession();
  const settings = useShopSettings();
  const items = useCartItems();
  const lines = useCartLines(items);
  const authenticated = session.status === "authenticated";
  const shipping = useShippingForm(authenticated);
  const [placedOrder, setPlacedOrder] = useState<Order | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [alert, setAlert] = useState<string | null>(null);

  async function placeOrder() {
    if (!shipping.validate()) {
      return;
    }
    setSubmitting(true);
    try {
      const order = await account.placeOrder({
        shipping: shipping.value,
        items: items.map(({ productId, selectedOptions, quantity }) => ({ productId, selectedOptions, quantity })),
      });
      cart.clear();
      setPlacedOrder(order);
    } catch (error) {
      if (!(error instanceof ApiError)) {
        throw error;
      }
      shipping.setErrors(shippingErrorsOf(error.fieldErrors));
      const itemError = Object.entries(error.fieldErrors).find(([field]) => field.startsWith("items"))?.[1];
      setAlert(describeMessage(itemError ?? error.userMessage));
    } finally {
      setSubmitting(false);
    }
  }

  if (placedOrder) {
    return (
      <Page>
        <Box>
          <h1>{t("thanks")}</h1>
          <p>
            {t.rich("toPay", {
              b: (chunks) => <b>{chunks}</b>,
              total: format.number(placedOrder.total, { style: "currency", currency: placedOrder.currency }),
            })}
          </p>
          <LottieAnimation name="thanks" />
          <ShopLink href="/products">{t("backToShop")}</ShopLink>
        </Box>
      </Page>
    );
  }

  const itemsTotal = lines?.reduce((sum, line) => sum + line.item.quantity * line.product.price, 0) ?? 0;

  return (
    <Page>
      {alert && <Alert message={alert} duration={6000} type="danger" onClose={() => setAlert(null)} />}
      <Box>
        <Title>{t("heading")}</Title>
        {lines === null && <Spinner size="2.5em" borderWidth="0.4em" />}
        {lines?.length === 0 && (
          <div>
            <h2>{t("empty")}</h2>
            <p>{t("emptyInvitation")}</p>
            <ShopLink href="/products">{t("backToShop")}</ShopLink>
          </div>
        )}
        {lines !== null && lines.length > 0 && (
          <Table>
            <thead>
              <tr>
                <th>{t("columns.preview")}</th>
                <th>{t("columns.name")}</th>
                <th>{t("columns.quantity")}</th>
                <th>{t("columns.price")}</th>
              </tr>
            </thead>
            <tbody>
              {lines.map(({ item, product }) => (
                <tr key={cartItemKey(item.productId, item.selectedOptions)}>
                  <td>
                    <Thumbnail>
                      <Image
                        src={product.images[0] ?? "/images/no-image-found.webp"}
                        alt={localized(product.name)}
                        fill
                        sizes="100px"
                        style={{ objectFit: "contain" }}
                      />
                    </Thumbnail>
                  </td>
                  <td>
                    {localized(product.name)}
                    <Options>{describeSelection(item.selection)}</Options>
                  </td>
                  <td>
                    <Button $size="s" $usage="primary" aria-label={t("decrease")} onClick={() => cart.remove(item)}>
                      -
                    </Button>
                    <QuantityLabel>{item.quantity}</QuantityLabel>
                    <Button
                      $size="s"
                      $usage="primary"
                      aria-label={t("increase")}
                      disabled={!canIncrease(item)}
                      onClick={() => cart.add(item)}
                    >
                      +
                    </Button>
                  </td>
                  <td>
                    <Price amount={item.quantity * product.price} />
                  </td>
                </tr>
              ))}
              {settings && (
                <>
                  <tr>
                    <td colSpan={3}>{t("shipping")}</td>
                    <td>
                      <Price amount={settings.shippingPrice} />
                    </td>
                  </tr>
                  <tr>
                    <td colSpan={3}>{t("total")}</td>
                    <td>
                      <Price amount={itemsTotal + settings.shippingPrice} />
                    </td>
                  </tr>
                </>
              )}
            </tbody>
          </Table>
        )}
      </Box>
      {lines?.length === 0 && (
        <Box>
          <Centered>
            <LottieAnimation name="cart" />
          </Centered>
        </Box>
      )}
      {lines !== null && lines.length > 0 && (
        <Box>
          <Title>{t("paymentHeading")}</Title>
          {!authenticated && (
            <>
              <p>{t("loginRequired")}</p>
              <Button $size="m" $usage="primary" onClick={() => signIn("page")}>
                {t("googleLogin")}
              </Button>
            </>
          )}
          {authenticated && shipping.status === "loading" && <Spinner size="2.5em" borderWidth="0.4em" />}
          {authenticated && shipping.status === "failed" && <LoadFailed />}
          {authenticated && shipping.status === "loaded" && (
            <>
              <ShippingDetailsForm value={shipping.value} errors={shipping.errors} onChange={shipping.onChange} />
              {settings?.checkoutEnabled === false && <p>{t("checkoutDisabled")}</p>}
              <Button
                $size="m"
                $usage="primary"
                disabled={submitting || settings?.checkoutEnabled !== true}
                onClick={() => void placeOrder()}
              >
                <IconCreditCart />
                {t("placeOrder")}
              </Button>
            </>
          )}
        </Box>
      )}
    </Page>
  );
}

function Page({ children }: Readonly<{ children: ReactNode }>) {
  const t = useTranslations("page.cart");
  return (
    <>
      <PageTitle title={t("title")} />
      <Layout>
        <DivCenter>
          <Wrapper>{children}</Wrapper>
        </DivCenter>
      </Layout>
    </>
  );
}
