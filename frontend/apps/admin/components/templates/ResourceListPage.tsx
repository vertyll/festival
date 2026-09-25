import Link from "next/link";
import { useTranslations } from "use-intl";
import type { Id } from "@festival/shared/api/types";
import { useLoader } from "@festival/shared/react/useLoader";
import { useConfirmedRemoval } from "@/lib/dialogs";
import Button from "../atoms/Button";
import DataTable, { type Column } from "../organisms/DataTable";
import Layout from "./Layout";

interface ResourceListPageProps<T extends { id: Id }> {
  title: string;
  basePath: string;
  addLabel: string;
  nameHeader: string;
  label: (item: T) => string;
  api: { list: () => Promise<T[]>; remove: (id: Id) => Promise<void> };
  extraColumns?: readonly Column<T>[];
}

export default function ResourceListPage<T extends { id: Id }>({
  title,
  basePath,
  addLabel,
  nameHeader,
  label,
  api,
  extraColumns = [],
}: Readonly<ResourceListPageProps<T>>) {
  const t = useTranslations("admin.actions");
  const items = useLoader(api.list);
  const removeConfirmed = useConfirmedRemoval();

  async function remove(item: T) {
    if (await removeConfirmed(label(item), () => api.remove(item.id))) {
      items.reload();
    }
  }

  const columns: Column<T>[] = [
    { header: nameHeader, render: label },
    ...extraColumns,
    {
      header: t("header"),
      render: (item) => (
        <>
          <Link href={`${basePath}/${item.id}`}>{t("edit")}</Link>
          <Button variant="danger" onClick={() => void remove(item)}>
            {t("delete")}
          </Button>
        </>
      ),
    },
  ];

  return (
    <Layout title={title}>
      <div className="my-3">
        <Link className="btn-primary" href={`${basePath}/new`}>
          {addLabel}
        </Link>
      </div>
      <DataTable state={items.state} itemKey={(item) => item.id} columns={columns} emptyMessage={t("empty")} />
    </Layout>
  );
}
