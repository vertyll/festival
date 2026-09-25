import { useTranslations } from "use-intl";
import DivCenter from "../atoms/DivCenter";
import DivText from "../atoms/DivText";
import PageTitle from "../atoms/PageTitle";
import Title from "../atoms/Title";
import Layout from "./Layout";

export type LegalDocument = "privacyPolicy" | "termsOfUse" | "regulations" | "bagPolicy";

type Block = { kind: "paragraph"; key: string } | { kind: "list"; key: string; items: number };

const PLACEHOLDER: readonly Block[] = [
  { kind: "paragraph", key: "paragraph1" },
  { kind: "paragraph", key: "paragraph2" },
  { kind: "list", key: "list1", items: 13 },
  { kind: "paragraph", key: "paragraph3" },
  { kind: "paragraph", key: "paragraph4" },
  { kind: "paragraph", key: "paragraph5" },
  { kind: "list", key: "list2", items: 6 },
  { kind: "list", key: "list3", items: 8 },
  { kind: "list", key: "list4", items: 8 },
  { kind: "paragraph", key: "paragraph6" },
  { kind: "paragraph", key: "paragraph7" },
  { kind: "paragraph", key: "paragraph8" },
  { kind: "paragraph", key: "paragraph9" },
  { kind: "paragraph", key: "paragraph10" },
];

export default function LegalPage({ document }: Readonly<{ document: LegalDocument }>) {
  const t = useTranslations("legal");
  return (
    <>
      <PageTitle title={t(`${document}.title`)} />
      <Layout>
        <DivCenter>
          <Title>{t(`${document}.heading`)}</Title>
          <PlaceholderText />
        </DivCenter>
      </Layout>
    </>
  );
}

function PlaceholderText() {
  const t = useTranslations("legal.placeholder");
  return (
    <DivText>
      {PLACEHOLDER.map((block) =>
        block.kind === "paragraph" ? (
          <p key={block.key}>{t(block.key)}</p>
        ) : (
          <ul key={block.key}>
            {Array.from({ length: block.items }, (_, index) => `${block.key}.item${index + 1}`).map((item) => (
              <li key={item}>{t(item)}</li>
            ))}
          </ul>
        )
      )}
    </DivText>
  );
}
