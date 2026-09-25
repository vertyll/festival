import type { GetServerSideProps } from "next";
import type { ReactNode } from "react";
import { useFormatter, useTranslations } from "use-intl";
import type { ArtistDetails } from "@festival/shared/api/types";
import { dateOnly } from "@festival/shared/format/format";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import DetailLayout from "@/components/templates/DetailLayout";
import { findOrNull, routeParam, serverApi } from "@/lib/serverApi";

export default function ArtistPage({ artist }: Readonly<{ artist: ArtistDetails }>) {
  const t = useTranslations("page.artist");
  const format = useFormatter();
  const localized = useLocalized();
  const bold = (chunks: ReactNode) => <b>{chunks}</b>;
  const noData = t("noData");
  return (
    <DetailLayout title={artist.name} images={artist.images} backLink="/lineup">
      <div>{t.rich("stage", { b: bold, stage: artist.stage ? localized(artist.stage.name) : noData })}</div>
      <div>
        {t.rich("concertDate", {
          b: bold,
          date: artist.concertDate ? format.dateTime(dateOnly(artist.concertDate), { dateStyle: "long" }) : noData,
        })}
      </div>
      <div>{t.rich("concertTime", { b: bold, time: artist.concertTime ?? noData })}</div>
      {artist.description && <div>{localized(artist.description)}</div>}
    </DetailLayout>
  );
}

export const getServerSideProps: GetServerSideProps<{ artist: ArtistDetails }, { id: string }> = async ({ params }) => {
  const artist = await findOrNull(() => serverApi().artist(routeParam(params, "id")));
  return artist ? { props: { artist } } : { notFound: true };
};
