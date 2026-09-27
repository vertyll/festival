import { useState } from "react";
import { useTranslations } from "use-intl";
import type { Translation } from "@festival/shared/api/types";
import Button from "@/components/atoms/Button";
import TranslationSpreadsheetActions from "@/components/organisms/TranslationSpreadsheetActions";
import TranslationEditor from "@/components/organisms/TranslationEditor";

function matches(translation: Translation, filter: string): boolean {
  const needle = filter.trim().toLowerCase();
  return [translation.key, ...Object.values(translation.messages)].some((text) => text.toLowerCase().includes(needle));
}

const PAGE_SIZE = 25;

export default function TranslationList({
  translations,
  reload,
}: Readonly<{
  translations: readonly Translation[];
  reload: () => void;
}>) {
  const t = useTranslations("admin.translations");
  const [filter, setFilter] = useState("");
  const [onlyCustomized, setOnlyCustomized] = useState(false);
  const [limit, setLimit] = useState(PAGE_SIZE);
  const found = translations.filter(
    (translation) => (!onlyCustomized || translation.customized) && matches(translation, filter)
  );

  return (
    <>
      <p className="text-sm text-neutral-600 my-2">{t("hint")}</p>
      <TranslationSpreadsheetActions onImported={reload} />
      <div className="flex flex-wrap items-center gap-4 my-2">
        <input
          value={filter}
          placeholder={t("filter")}
          aria-label={t("filter")}
          onChange={(event) => {
            setFilter(event.target.value);
            setLimit(PAGE_SIZE);
          }}
        />
        <label className="flex items-center gap-2">
          <input
            type="checkbox"
            className="w-auto mt-0"
            checked={onlyCustomized}
            onChange={(event) => setOnlyCustomized(event.target.checked)}
          />
          {t("onlyCustomized")}
        </label>
        <span className="text-sm text-neutral-500">
          {t("count", { shown: found.length, total: translations.length })}
        </span>
      </div>
      {found.length === 0 && <p>{t("empty")}</p>}
      {found.slice(0, limit).map((translation) => (
        <TranslationEditor
          key={`${translation.key}:${translation.updatedAt}`}
          translation={translation}
          onSaved={reload}
        />
      ))}
      {found.length > limit && <Button onClick={() => setLimit(limit + PAGE_SIZE)}>{t("showMore")}</Button>}
    </>
  );
}
