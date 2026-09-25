import { useTranslations } from "use-intl";
import type { GetServerSideProps } from "next";
import type { News } from "@festival/shared/api/types";
import NewsBox from "@/components/organisms/NewsBox";
import RevealGrid from "@/components/organisms/RevealGrid";
import ListingPage from "@/components/templates/ListingPage";
import { serverApi } from "@/lib/serverApi";

export default function NewsListPage({ news }: Readonly<{ news: News[] }>) {
  const t = useTranslations("page.news");
  return (
    <ListingPage
      title={t("title")}
      heading={t("heading")}
      bannerUrl="/images/banernewsy.webp"
      emptyMessage={news.length > 0 ? null : t("empty")}
    >
      <RevealGrid items={news} itemKey={(item) => item.id} columns={{ 0: 1, 985: Math.min(news.length, 2) }} gap={50}>
        {(item) => <NewsBox news={item} />}
      </RevealGrid>
    </ListingPage>
  );
}

export const getServerSideProps: GetServerSideProps<{ news: News[] }> = async () => ({
  props: { news: await serverApi().newsList() },
});
