import { useTranslations } from "use-intl";
import type { GetServerSideProps } from "next";
import type { Artist } from "@festival/shared/api/types";
import ArtistBox from "@/components/organisms/ArtistBox";
import RevealGrid, { WIDE_GRID } from "@/components/organisms/RevealGrid";
import ListingPage from "@/components/templates/ListingPage";
import { serverApi } from "@/lib/serverApi";

export default function LineUpPage({ artists }: Readonly<{ artists: Artist[] }>) {
  const t = useTranslations("page.lineup");
  return (
    <ListingPage
      title={t("title")}
      heading={t("heading")}
      bannerUrl="/images/banerlineup.webp"
      emptyMessage={artists.length > 0 ? null : t("empty")}
    >
      <RevealGrid items={artists} itemKey={(artist) => artist.id} columns={WIDE_GRID} gap={25}>
        {(artist) => <ArtistBox artist={artist} />}
      </RevealGrid>
    </ListingPage>
  );
}

export const getServerSideProps: GetServerSideProps<{ artists: Artist[] }> = async () => ({
  props: { artists: await serverApi().artists() },
});
