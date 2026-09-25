import { useState, type ChangeEvent } from "react";
import { useTranslations } from "use-intl";
import { admin } from "@/lib/api";
import { useDialogs } from "@/lib/dialogs";
import Spinner from "../atoms/Spinner";

const XLSX_TYPES = ".xlsx,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

export default function TranslationSpreadsheetActions({ onImported }: Readonly<{ onImported: () => void }>) {
  const t = useTranslations("admin.translations.spreadsheet");
  const { notifyError, notifySuccess } = useDialogs();
  const [importing, setImporting] = useState(false);

  async function importFile(event: ChangeEvent<HTMLInputElement>) {
    const input = event.currentTarget;
    const file = input.files?.[0];
    if (!file) {
      return;
    }
    setImporting(true);
    try {
      const result = await admin.translations.importSpreadsheet(file);
      notifySuccess(t("imported"), t("summary", { updated: result.updated, unchanged: result.unchanged }));
      onImported();
    } catch (error) {
      notifyError(error);
    } finally {
      setImporting(false);
      input.value = "";
    }
  }

  return (
    <div className="flex flex-wrap items-center gap-2 my-2">
      <a className="btn-primary" href={admin.translations.exportUrl} download>
        {t("export")}
      </a>
      <label className={`btn-primary cursor-pointer ${importing ? "pointer-events-none opacity-50" : ""}`}>
        {t("import")}
        <input
          type="file"
          accept={XLSX_TYPES}
          className="hidden"
          disabled={importing}
          onChange={(event) => void importFile(event)}
        />
      </label>
      {importing && <Spinner />}
      <p className="w-full text-sm text-neutral-600">{t("hint")}</p>
    </div>
  );
}
