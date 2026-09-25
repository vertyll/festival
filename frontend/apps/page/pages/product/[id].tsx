import type { GetServerSideProps } from "next";
import { useState } from "react";
import { useTranslations } from "use-intl";
import styled from "styled-components";
import type { ProductDetails, SelectedOptions } from "@festival/shared/api/types";
import { useDescribeSelection, useLocalized, useMessage } from "@festival/shared/i18n/IntlSetup";
import { Alert, type AlertType } from "@/components/atoms/Alert";
import Button from "@/components/atoms/Button";
import DivCenter from "@/components/atoms/DivCenter";
import PageTitle from "@/components/atoms/PageTitle";
import Price from "@/components/atoms/Price";
import SingleBox from "@/components/atoms/SingleBox";
import StyledDescriptionBox from "@/components/atoms/StyledDescriptionBox";
import { IconCart } from "@/components/atoms/icons";
import ImageGallery from "@/components/organisms/ImageGallery";
import Layout from "@/components/templates/Layout";
import { cart } from "@/lib/cart";
import { findOrNull, routeParam, serverApi } from "@/lib/serverApi";
import type { WithShopSettings } from "@/lib/shopSettings";
import { isSelectable, selectedVariant, variantSelection } from "@/lib/variants";

const ColWrapper = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  max-width: 1000px;
  width: 100%;
  gap: 70px;
  margin: 50px 5px;

  @media screen and (min-width: 768px) {
    grid-template-columns: 1.1fr 0.9fr;
  }
`;

const DescriptionWrapper = styled.div`
  display: flex;
  width: 100%;
  align-items: center;
  justify-content: center;
  margin: 0 5px;
`;

const Column = styled.div`
  display: flex;
  gap: 20px;
  flex-direction: column;
`;

const PriceText = styled.span`
  font-size: 1.4rem;
`;

const OptionButton = styled.button<{ $selected: boolean }>`
  padding: 15px 30px;
  margin: 5px;
  border: 1.5px solid ${(props) => (props.$selected ? "var(--dark-text-color)" : "var(--border-color-light)")};
  background-color: var(--light-color);
  border-radius: 5px;
  color: var(--dark-text-color);
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover:enabled {
    border-color: var(--dark-text-color);
  }

  &:disabled {
    border-color: var(--no-properties-color);
    cursor: not-allowed;
    opacity: 0.5;
  }
`;

const OptionValues = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 1px;
`;

const Stock = styled.span<{ $available: boolean }>`
  color: ${(props) => (props.$available ? "green" : "red")};
`;

const Title = styled.h1`
  font-size: 2em;
`;

interface ProductPageProps extends WithShopSettings {
  product: ProductDetails;
}

export default function ProductPage({ product }: Readonly<ProductPageProps>) {
  const t = useTranslations("page.product");
  const localized = useLocalized();
  const describeSelection = useDescribeSelection();
  const [selected, setSelected] = useState<SelectedOptions>({});
  const describeMessage = useMessage();
  const [alert, setAlert] = useState<{ text: string; type: AlertType } | null>(null);
  const variant = selectedVariant(product, selected);
  const variantStocks = product.variants.filter((item) => item.stock !== null);
  const name = localized(product.name);

  function addToCart(valueCodes: readonly string[]) {
    const error = cart.add({
      productId: product.id,
      selectedOptions: selected,
      selection: variantSelection(product, valueCodes),
    });
    setAlert(error ? { text: describeMessage(error), type: "danger" } : { text: t("added"), type: "success" });
  }

  return (
    <>
      <PageTitle title={name} />
      <Layout>
        {alert && <Alert message={alert.text} onClose={() => setAlert(null)} duration={3000} type={alert.type} />}
        <DivCenter>
          <ColWrapper>
            <SingleBox>
              <ImageGallery images={product.images} alt={name} maxHeight={200} />
            </SingleBox>
            <Column>
              <Title>{name}</Title>
              <div>
                {t.rich("category", {
                  b: (chunks) => <b>{chunks}</b>,
                  path:
                    product.categoryPath.length > 0
                      ? product.categoryPath.map((category) => localized(category.name)).join(" / ")
                      : t("noCategory"),
                })}
              </div>
              {product.options.map((option) => (
                <div key={option.code}>
                  <p>{t("option", { option: localized(option.name) })}</p>
                  <OptionValues>
                    {option.values.map((value) => (
                      <OptionButton
                        key={value.code}
                        type="button"
                        $selected={selected[option.code] === value.code}
                        aria-pressed={selected[option.code] === value.code}
                        disabled={!isSelectable(product, selected, option.code, value.code)}
                        onClick={() => setSelected({ ...selected, [option.code]: value.code })}
                      >
                        {localized(value.label)}
                      </OptionButton>
                    ))}
                  </OptionValues>
                </div>
              ))}
              {product.options.length > 0 &&
                variantStocks.map((item) => (
                  <div key={item.valueCodes.join("|")}>
                    {t.rich("variantStock", {
                      variant: describeSelection(variantSelection(product, item.valueCodes)),
                      stock: item.stock ?? 0,
                      amount: (chunks) => <Stock $available={item.available}>{chunks}</Stock>,
                    })}
                  </div>
                ))}
              {product.totalStock !== null && (
                <div>
                  {t.rich("totalStock", {
                    stock: product.totalStock,
                    strong: (chunks) => <strong>{chunks}</strong>,
                    amount: (chunks) => <Stock $available={(product.totalStock ?? 0) > 0}>{chunks}</Stock>,
                  })}
                </div>
              )}
              <PriceText>
                {t.rich("price", { b: (chunks) => <b>{chunks}</b> })} <Price amount={product.price} />
              </PriceText>
              <Button
                onClick={() => variant && addToCart(variant.valueCodes)}
                $size="m"
                $usage="primary"
                disabled={!variant?.available}
              >
                <IconCart />
                {variant === null || variant.available ? t("addToCart") : t("unavailable")}
              </Button>
            </Column>
          </ColWrapper>
          {product.description && (
            <>
              <h3>{t("description")}</h3>
              <DescriptionWrapper>
                <StyledDescriptionBox>{localized(product.description)}</StyledDescriptionBox>
              </DescriptionWrapper>
            </>
          )}
        </DivCenter>
      </Layout>
    </>
  );
}

export const getServerSideProps: GetServerSideProps<ProductPageProps, { id: string }> = async ({ params }) => {
  const api = serverApi();
  const [product, shopSettings] = await Promise.all([
    findOrNull(() => api.product(routeParam(params, "id"))),
    api.settings(),
  ]);
  return product ? { props: { product, shopSettings } } : { notFound: true };
};
