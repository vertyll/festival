import type { ReactNode } from "react";
import { useTranslations } from "use-intl";
import type { Loaded } from "@festival/shared/react/useLoader";
import Spinner from "../atoms/Spinner";

export interface Column<T> {
  header: string;
  render: (item: T) => ReactNode;
}

interface DataTableProps<T> {
  state: Loaded<readonly T[]>;
  itemKey: (item: T) => string;
  columns: readonly Column<T>[];
  emptyMessage: string;
}

export default function DataTable<T>({ state, itemKey, columns, emptyMessage }: Readonly<DataTableProps<T>>) {
  const t = useTranslations("common");
  const items = state.status === "loaded" ? state.data : null;
  return (
    <table className="primary-table mt-5">
      <thead>
        <tr>
          {columns.map((column) => (
            <th key={column.header}>{column.header}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {state.status === "error" && (
          <tr>
            <td colSpan={columns.length}>{t("loadFailed")}</td>
          </tr>
        )}
        {state.status === "loading" && (
          <tr>
            <td colSpan={columns.length}>
              <Spinner />
            </td>
          </tr>
        )}
        {items?.length === 0 && (
          <tr>
            <td colSpan={columns.length}>{emptyMessage}</td>
          </tr>
        )}
        {items?.map((item) => (
          <tr key={itemKey(item)}>
            {columns.map((column) => (
              <td key={column.header} data-label={column.header}>
                {column.render(item)}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
