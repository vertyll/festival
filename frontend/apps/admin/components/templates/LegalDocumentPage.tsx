import Head from "next/head";
import Link from "next/link";
import { useTranslations } from "use-intl";
import LegalParagraphs from "@festival/shared/react/LegalParagraphs";
import LanguageSwitcher from "../molecules/LanguageSwitcher";

export type AdminLegalDocument = "termsOfUse" | "privacyPolicy";

export default function LegalDocumentPage({ document }: Readonly<{ document: AdminLegalDocument }>) {
  const t = useTranslations("legal");
  const layout = useTranslations("admin.layout");
  return (
    <>
      <Head>
        <title>{layout("title", { page: t(`${document}.title`) })}</title>
      </Head>
      <main className="min-h-screen bg-gray-50 px-4 py-10">
        <div className="mx-auto max-w-3xl">
          <div className="mb-6 flex items-center justify-between">
            <Link href="/" className="text-indigo-700 hover:underline">
              ← {layout("back")}
            </Link>
            <LanguageSwitcher tone="onLight" />
          </div>
          <h1 className="mb-6 text-3xl font-bold">{t(`${document}.title`)}</h1>
          <div className="flex flex-col gap-4 leading-relaxed text-gray-800">
            <LegalParagraphs content={t(`${document}.content`)} linkClassName="text-indigo-700 underline" />
          </div>
        </div>
      </main>
    </>
  );
}
