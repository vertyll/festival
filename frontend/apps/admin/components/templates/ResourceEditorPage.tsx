import { useRouter } from "next/router";
import type { ReactNode } from "react";
import { useTranslations } from "use-intl";
import type { Id } from "@festival/shared/api/types";
import { useLoader } from "@festival/shared/react/useLoader";
import Spinner from "../atoms/Spinner";
import Layout from "./Layout";

interface ResourceEditorPageProps<T> {
  title: (item: T) => string;
  load: (id: Id) => Promise<T>;
  children: (item: T) => ReactNode;
}

export default function ResourceEditorPage<T>({ title, load, children }: Readonly<ResourceEditorPageProps<T>>) {
  const t = useTranslations("admin.editor");
  const router = useRouter();
  const id = router.query.id;
  const item = useLoader(() => load(id as Id), typeof id === "string");

  if (item.state.status === "error") {
    return (
      <Layout title={t("notFoundTitle")}>
        <p>{t("notFound")}</p>
      </Layout>
    );
  }
  return (
    <Layout title={item.data ? title(item.data) : t("loadingTitle")}>
      {item.data ? children(item.data) : <Spinner />}
    </Layout>
  );
}
