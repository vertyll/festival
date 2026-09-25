import Head from "next/head";
import { useTranslations } from "use-intl";

export default function PageTitle({ title }: Readonly<{ title: string }>) {
  const t = useTranslations("common.meta");
  return (
    <Head>
      <title>{t("title", { page: title })}</title>
    </Head>
  );
}
