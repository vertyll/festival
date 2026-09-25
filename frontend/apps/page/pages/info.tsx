import styled from "styled-components";
import { useTranslations } from "use-intl";
import DivCenter from "@/components/atoms/DivCenter";
import PageTitle from "@/components/atoms/PageTitle";
import Title from "@/components/atoms/Title";
import TitleBanner from "@/components/atoms/TitleBanner";
import Layout from "@/components/templates/Layout";

const ImageWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  text-align: center;
  font-weight: bold;
  img {
    max-height: 100%;
    max-width: 100%;
  }
  @media screen and (min-width: 1000px) {
    max-width: 60%;
  }
`;

const InfoWrapper = styled.div`
  font-weight: bold;
  @media screen and (min-width: 1000px) {
    max-width: 60%;
  }
`;

const PRACTICAL_PARAGRAPHS = ["openingHours", "identityDocument", "minors"] as const;

export default function InfoPage() {
  const t = useTranslations("page.info");
  return (
    <>
      <PageTitle title={t("title")} />
      <Layout>
        <TitleBanner imageUrl="/images/banerinfo.webp" />
        <DivCenter>
          <Title>{t("practicalHeading")}</Title>
          <InfoWrapper>
            {PRACTICAL_PARAGRAPHS.map((paragraph) => (
              <p key={paragraph}>{t(paragraph)}</p>
            ))}
          </InfoWrapper>
          <Title>{t("mapHeading")}</Title>
          <ImageWrapper>
            <p>{t("mapDescription")}</p>
            {/* eslint-disable-next-line @next/next/no-img-element -- statyczna grafika z public/ */}
            <img src="/images/mapka.webp" alt={t("mapAlt")} />
          </ImageWrapper>
        </DivCenter>
      </Layout>
    </>
  );
}
