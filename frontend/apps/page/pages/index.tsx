import { useTranslations } from "use-intl";
import type { GetServerSideProps } from "next";
import type { Artist, News, ProductCard } from "@festival/shared/api/types";
import PageTitle from "@/components/atoms/PageTitle";
import DivCenter from "@/components/atoms/DivCenter";
import Title from "@/components/atoms/Title";
import ArtistBox from "@/components/organisms/ArtistBox";
import Banner from "@/components/organisms/Banner";
import NewsBox from "@/components/organisms/NewsBox";
import Newsletter from "@/components/organisms/Newsletter";
import ProductBox from "@/components/organisms/ProductBox";
import RevealGrid, { WIDE_GRID } from "@/components/organisms/RevealGrid";
import Layout from "@/components/templates/Layout";
import { serverApi } from "@/lib/serverApi";
import type { WithShopSettings } from "@/lib/shopSettings";

interface HomePageProps extends WithShopSettings {
  products: ProductCard[];
  artists: Artist[];
  news: News[];
}

export default function HomePage({ products, artists, news }: Readonly<HomePageProps>) {
  const t = useTranslations("page.home");
  return (
    <>
      <PageTitle title={t("title")} />
      <Layout>
        <Banner />
        <DivCenter>
          {artists.length > 0 && (
            <>
              <Title>{t("newArtists")}</Title>
              <RevealGrid items={artists} itemKey={(artist) => artist.id} columns={{ 0: 1, 768: 2 }} gap={50}>
                {(artist) => <ArtistBox artist={artist} />}
              </RevealGrid>
            </>
          )}
          {products.length > 0 && (
            <>
              <Title>{t("newProducts")}</Title>
              <RevealGrid items={products} itemKey={(product) => product.id} columns={WIDE_GRID} gap={50}>
                {(product) => <ProductBox product={product} />}
              </RevealGrid>
            </>
          )}
          {news.length > 0 && (
            <>
              <Title>{t("newNews")}</Title>
              <RevealGrid
                items={news}
                itemKey={(item) => item.id}
                columns={{ 0: 1, 985: Math.min(news.length, 2) }}
                gap={50}
              >
                {(item) => <NewsBox news={item} />}
              </RevealGrid>
            </>
          )}
        </DivCenter>
        <Newsletter />
      </Layout>
    </>
  );
}

export const getServerSideProps: GetServerSideProps<HomePageProps> = async () => {
  const api = serverApi();
  const [products, artists, news, shopSettings] = await Promise.all([
    api.products({ limit: 8 }),
    api.artists(4),
    api.newsList(2),
    api.settings(),
  ]);
  return { props: { products, artists, news, shopSettings } };
};
