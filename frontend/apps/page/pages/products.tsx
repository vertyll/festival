import { useTranslations } from "use-intl";
import type { GetServerSideProps } from "next";
import type { ProductCard } from "@festival/shared/api/types";
import ProductBox from "@/components/organisms/ProductBox";
import RevealGrid, { WIDE_GRID } from "@/components/organisms/RevealGrid";
import ListingPage from "@/components/templates/ListingPage";
import { serverApi } from "@/lib/serverApi";
import type { WithShopSettings } from "@/lib/shopSettings";

interface ProductsPageProps extends WithShopSettings {
  products: ProductCard[];
}

export default function ProductsPage({ products }: Readonly<ProductsPageProps>) {
  const t = useTranslations("page.products");
  return (
    <ListingPage
      title={t("title")}
      heading={t("heading")}
      bannerUrl="/images/banermerch.webp"
      emptyMessage={products.length > 0 ? null : t("empty")}
    >
      <RevealGrid items={products} itemKey={(product) => product.id} columns={WIDE_GRID} gap={50}>
        {(product) => <ProductBox product={product} />}
      </RevealGrid>
    </ListingPage>
  );
}

export const getServerSideProps: GetServerSideProps<ProductsPageProps> = async () => {
  const api = serverApi();
  const [products, shopSettings] = await Promise.all([api.products(), api.settings()]);
  return { props: { products, shopSettings } };
};
