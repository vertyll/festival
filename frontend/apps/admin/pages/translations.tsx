import { useState } from "react";
import { useFormatter, useTranslations } from "use-intl";
import type { Translation, TranslationRequest } from "@festival/shared/api/types";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import { LIMITS, localizedText } from "@festival/shared/validation/validation";
import Button from "@/components/atoms/Button";
import LoadFailed from "@/components/atoms/LoadFailed";
import Spinner from "@/components/atoms/Spinner";
import LocalizedTextField from "@/components/molecules/LocalizedTextField";
import TranslationSpreadsheetActions from "@/components/organisms/TranslationSpreadsheetActions";
import Layout from "@/components/templates/Layout";
import { admin } from "@/lib/api";
import { useDialogs } from "@/lib/dialogs";
import { useForm } from "@/lib/useForm";

const RULES = { messages: [localizedText(LIMITS.descriptionMaxLength)] };

function matches(translation: Translation, filter: string): boolean {
  const needle = filter.trim().toLowerCase();
  return [translation.key, ...Object.values(translation.messages)].some((text) => text.toLowerCase().includes(needle));
}

function TranslationEditor({ translation, onSaved }: Readonly<{ translation: Translation; onSaved: () => void }>) {
  const t = useTranslations("admin.translations");
  const tActions = useTranslations("admin.actions");
  const format = useFormatter();
  const { notifyError } = useDialogs();
  const form = useForm<TranslationRequest>({ messages: translation.messages }, RULES);

  async function save() {
    if (await form.submit((request) => admin.translations.update(translation.key, request))) {
      onSaved();
    }
  }

  async function reset() {
    try {
      await admin.translations.reset(translation.key);
      onSaved();
    } catch (error) {
      notifyError(error);
    }
  }

  return (
    <div className="border border-neutral-300 rounded-md p-2 my-2">
      <div className="flex flex-wrap justify-between gap-2">
        <code className="font-bold">{translation.key}</code>
        <span className="text-sm text-neutral-500">
          {translation.customized
            ? t("customized", {
                date: format.dateTime(new Date(translation.updatedAt), { dateStyle: "medium", timeStyle: "short" }),
              })
            : t("default")}
        </span>
      </div>
      <LocalizedTextField
        label={t("messages")}
        value={form.values.messages}
        rows={2}
        sideBySide
        onChange={(messages) => form.set("messages", messages)}
        errors={form.localizedErrors("messages")}
      />
      <div className="flex gap-1">
        <Button disabled={form.submitting} onClick={() => void save()}>
          {tActions("save")}
        </Button>
        {translation.customized && (
          <Button variant="danger" onClick={() => void reset()}>
            {t("reset")}
          </Button>
        )}
      </div>
    </div>
  );
}

const PAGE_SIZE = 25;

function TranslationList({ translations, reload }: Readonly<{
  translations: readonly Translation[];
  reload: () => void
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

export default function TranslationsPage() {
  const t = useTranslations("admin.translations");
  const translations = useLoader(admin.translations.list);
  return (
    <Layout title={t("title")}>
      <LoadState state={translations.state} loading={<Spinner />} failed={<LoadFailed />}>
        {(data) => <TranslationList translations={data} reload={translations.reload} />}
      </LoadState>
    </Layout>
  );
}
