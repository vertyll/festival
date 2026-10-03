import { useTranslations } from "use-intl";
import LegalParagraphs from "@festival/shared/react/LegalParagraphs";
import DivCenter from "../atoms/DivCenter";
import DivText from "../atoms/DivText";
import PageTitle from "../atoms/PageTitle";
import Title from "../atoms/Title";
import Layout from "./Layout";

export type LegalDocument = "privacyPolicy" | "termsOfUse" | "regulations" | "bagPolicy";

export default function LegalPage({ document }: Readonly<{ document: LegalDocument }>) {
  const t = useTranslations("legal");
  return (
    <>
      <PageTitle title={t(`${document}.title`)} />
      <Layout>
        <DivCenter>
          <Title>{t(`${document}.heading`)}</Title>
          <DivText>
            <LegalParagraphs content={t(`${document}.content`)} />
          </DivText>
        </DivCenter>
      </Layout>
    </>
  );
}
