import styled from "styled-components";
import { useTranslations } from "use-intl";
import LegalParagraphs, { type LegalDocumentName } from "@festival/shared/react/LegalParagraphs";
import PageTitle from "../atoms/PageTitle";
import Title from "../atoms/Title";
import Layout from "./Layout";

export type LegalDocument = LegalDocumentName;

const Article = styled.article`
  box-sizing: border-box;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 0 24px;
  line-height: 1.6;

  h2 {
    margin: 24px 0 4px;
    font-size: 1.15rem;
  }

  h2 + p {
    margin-top: 0;
  }
`;

const Modified = styled.p`
  margin-top: -16px;
  text-align: center;
  font-size: 0.875rem;
  color: var(--gray-color);
`;

export default function LegalPage({ document }: Readonly<{ document: LegalDocument }>) {
  const t = useTranslations("legal");
  return (
    <>
      <PageTitle title={t(`${document}.title`)} />
      <Layout>
        <Article>
          <Title>{t(`${document}.heading`)}</Title>
          <Modified>
            {t("lastModified")}: {t(`${document}.lastModified`)}
          </Modified>
          <LegalParagraphs document={document} t={t} />
        </Article>
      </Layout>
    </>
  );
}
