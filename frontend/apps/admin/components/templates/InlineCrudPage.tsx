import { useState, type ReactNode, type SubmitEvent } from "react";
import { useTranslations } from "use-intl";
import type { Id } from "@festival/shared/api/types";
import { useLoader } from "@festival/shared/react/useLoader";
import type { Rules } from "@festival/shared/validation/validation";
import { useConfirmedRemoval } from "@/lib/dialogs";
import { useForm, type Form } from "@/lib/useForm";
import Button from "../atoms/Button";
import DataTable, { type Column } from "../organisms/DataTable";
import Layout from "./Layout";

interface CrudApi<T, R> {
  list: () => Promise<T[]>;
  create: (request: R) => Promise<T>;
  update: (id: Id, request: R) => Promise<T>;
  remove: (id: Id) => Promise<void>;
}

interface InlineCrudPageProps<T extends { id: Id }, R extends object> {
  title: string;
  api: CrudApi<T, R>;
  emptyRequest: R;
  toRequest: (item: T) => R;
  rules: Rules<R>;
  label: (item: T) => string;
  createHeading: string;
  columns: readonly Column<T>[];
  renderFields: (form: Form<R>, items: readonly T[] | null, edited: T | null) => ReactNode;
}

export default function InlineCrudPage<T extends { id: Id }, R extends object>({
  title,
  api,
  emptyRequest,
  toRequest,
  rules,
  label,
  createHeading,
  columns,
  renderFields,
}: Readonly<InlineCrudPageProps<T, R>>) {
  const t = useTranslations("admin.actions");
  const items = useLoader(api.list);
  const removeConfirmed = useConfirmedRemoval();
  const form = useForm<R>(emptyRequest, rules);
  const [edited, setEdited] = useState<T | null>(null);

  function startEditing(item: T | null) {
    setEdited(item);
    form.reset(item ? toRequest(item) : emptyRequest);
  }

  async function save(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    const saved = await form.submit((request) => (edited ? api.update(edited.id, request) : api.create(request)));
    if (saved) {
      startEditing(null);
      items.reload();
    }
  }

  async function remove(item: T) {
    if (await removeConfirmed(label(item), () => api.remove(item.id))) {
      items.reload();
    }
  }

  const tableColumns: Column<T>[] = [
    ...columns,
    {
      header: t("header"),
      render: (item) => (
        <>
          <Button className="mr-2" onClick={() => startEditing(item)}>
            {t("edit")}
          </Button>
          <Button variant="danger" onClick={() => void remove(item)}>
            {t("delete")}
          </Button>
        </>
      ),
    },
  ];

  return (
    <Layout title={title}>
      <form onSubmit={(event) => void save(event)} noValidate>
        <h2 className="title-title">{edited ? t("editing", { label: label(edited) }) : createHeading}</h2>
        {renderFields(form, items.data, edited)}
        <div className="flex gap-1">
          {edited && (
            <Button variant="danger" onClick={() => startEditing(null)}>
              {t("cancel")}
            </Button>
          )}
          <Button type="submit" disabled={form.submitting}>
            {t("save")}
          </Button>
        </div>
      </form>
      {!edited && (
        <DataTable state={items.state} itemKey={(item) => item.id} columns={tableColumns} emptyMessage={t("empty")} />
      )}
    </Layout>
  );
}
