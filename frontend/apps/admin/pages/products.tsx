import { useFormatter, useTranslations } from "use-intl";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import ResourceListPage from "@/components/templates/ResourceListPage";
import { admin } from "@/lib/api";

export default function ProductListPage() {
  const t = useTranslations("admin.products");
  const localized = useLocalized();
  const format = useFormatter();
  return (
    <ResourceListPage
      title={t("title")}
      basePath="/products"
      addLabel={t("add")}
      nameHeader={t("name")}
      label={(item) => localized(item.name)}
      api={admin.products}
      extraColumns={[
        {
          header: t("price"),
          render: (product) => format.number(product.price, { minimumFractionDigits: 2, maximumFractionDigits: 2 }),
        },
        {
          header: t("stock"),
          render: (product) => format.number(product.variants.reduce((sum, variant) => sum + variant.stock, 0)),
        },
      ]}
    />
  );
}
