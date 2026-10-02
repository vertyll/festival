import { useTranslations } from "use-intl";
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
            {t(`${document}.content`)
              .split("\n\n")
              .map((paragraph) => (
                <p key={paragraph}>{paragraph}</p>
              ))}
          </DivText>
        </DivCenter>
      </Layout>
    </>
  );
}
