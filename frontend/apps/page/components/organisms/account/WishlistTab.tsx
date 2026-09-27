import { useTranslations } from "use-intl";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import LoadFailed from "@/components/atoms/LoadFailed";
import ProductBox from "@/components/organisms/ProductBox";
import RevealGrid from "@/components/organisms/RevealGrid";
import { account } from "@/lib/api";
import { useWishlist } from "@/lib/wishlist";
import AccountSpinner from "./AccountSpinner";
import { TabContent } from "./TabContent";

export default function WishlistTab() {
  const t = useTranslations("page.account");
  const products = useLoader(account.wishlist);
  const { productIds } = useWishlist();
  return (
    <LoadState state={products.state} loading={<AccountSpinner />} failed={<LoadFailed />}>
      {(data) => {
        const wished = data.filter((product) => productIds.has(product.id));
        return (
          <TabContent>
            {wished.length === 0 ? (
              <p>{t("noWishlist")}</p>
            ) : (
              <RevealGrid items={wished} itemKey={(product) => product.id} columns={{ 0: 2, 1200: 3 }} gap={20}>
                {(product) => <ProductBox product={product} />}
              </RevealGrid>
            )}
          </TabContent>
        );
      }}
    </LoadState>
  );
}
