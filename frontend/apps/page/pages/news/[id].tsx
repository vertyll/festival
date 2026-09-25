import type { GetServerSideProps } from "next";
import { useFormatter, useTranslations } from "use-intl";
import type { News } from "@festival/shared/api/types";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import DetailLayout from "@/components/templates/DetailLayout";
import { findOrNull, routeParam, serverApi } from "@/lib/serverApi";

export default function NewsPage({ news }: Readonly<{ news: News }>) {
  const t = useTranslations("page.newsDetails");
  const format = useFormatter();
  const localized = useLocalized();
  return (
    <DetailLayout title={localized(news.name)} images={news.images} backLink="/news">
      <div>
        {t.rich("published", {
          b: (chunks) => <b>{chunks}</b>,
          date: format.dateTime(new Date(news.createdAt), { dateStyle: "medium", timeStyle: "short" }),
        })}
      </div>
      <div>{localized(news.description)}</div>
    </DetailLayout>
  );
}

export const getServerSideProps: GetServerSideProps<{ news: News }, { id: string }> = async ({ params }) => {
  const news = await findOrNull(() => serverApi().news(routeParam(params, "id")));
  return news ? { props: { news } } : { notFound: true };
};
