import { useTranslations } from "use-intl";
import type { GetServerSideProps } from "next";
import styled from "styled-components";
import type { Sponsor } from "@festival/shared/api/types";
import RevealGrid, { WIDE_GRID } from "@/components/organisms/RevealGrid";
import SponsorBox from "@/components/organisms/SponsorBox";
import ListingPage from "@/components/templates/ListingPage";
import { serverApi } from "@/lib/serverApi";

const Thanks = styled.h2`
  font-size: 1.5em;
  font-family: "Almendra", serif;
`;

export default function SponsorsPage({ sponsors }: Readonly<{ sponsors: Sponsor[] }>) {
  const t = useTranslations("page.sponsors");
  return (
    <ListingPage title={t("title")} heading={t("heading")} emptyMessage={sponsors.length > 0 ? null : t("empty")}>
      <Thanks>{t("thanks")}</Thanks>
      <RevealGrid items={sponsors} itemKey={(sponsor) => sponsor.id} columns={WIDE_GRID} gap={15}>
        {(sponsor) => <SponsorBox sponsor={sponsor} />}
      </RevealGrid>
    </ListingPage>
  );
}

export const getServerSideProps: GetServerSideProps<{ sponsors: Sponsor[] }> = async () => ({
  props: { sponsors: await serverApi().sponsors() },
});
