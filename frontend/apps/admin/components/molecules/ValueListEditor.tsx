import { useState } from "react";
import { useTranslations } from "use-intl";
import { LANGUAGES, type LocalizedText, type Message, type OptionValue } from "@festival/shared/api/types";
import { emptyLocalizedText } from "@festival/shared/i18n/localized";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import { LIMITS, localizedText, message } from "@festival/shared/validation/validation";
import type { LocalizedErrors } from "@/lib/useForm";
import Button from "../atoms/Button";
import ErrorMessage from "../atoms/ErrorMessage";
import LocalizedTextField from "./LocalizedTextField";

const validLabel = localizedText(LIMITS.attributeValueMaxLength);

const MAX_CODE_LENGTH = 40;

function slug(text: string): string {
  return text
    .normalize("NFKD")
    .replace(/[̀-ͯ]/g, "")
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-|-$/g, "")
    .slice(0, MAX_CODE_LENGTH - 4);
}

function uniqueCode(label: LocalizedText, taken: readonly string[]): string {
  const base = slug(label.en) || slug(label.pl) || "value";
  let code = base;
  for (let suffix = 2; taken.includes(code); suffix++) {
    code = `${base}-${suffix}`;
  }
  return code;
}

function sameLabel(first: LocalizedText, second: LocalizedText): boolean {
  return LANGUAGES.some((language) => first[language].trim().toLowerCase() === second[language].trim().toLowerCase());
}

interface ValueListEditorProps {
  values: readonly OptionValue[];
  onChange: (values: OptionValue[]) => void;
  error: Message | undefined;
}

export default function ValueListEditor({ values, onChange, error }: Readonly<ValueListEditorProps>) {
  const t = useTranslations("admin.attributes");
  const tActions = useTranslations("admin.actions");
  const localized = useLocalized();
  const [draft, setDraft] = useState<LocalizedText>(emptyLocalizedText());
  const [editedCode, setEditedCode] = useState<string | null>(null);
  const [draftErrors, setDraftErrors] = useState<LocalizedErrors>({});

  function commit() {
    const label = Object.fromEntries(LANGUAGES.map((language) => [language, draft[language].trim()])) as LocalizedText;
    const invalid = validLabel(label);
    const duplicated = values.some((existing) => existing.code !== editedCode && sameLabel(existing.label, label));
    if (invalid?.language) {
      setDraftErrors({ [invalid.language]: invalid });
      return;
    }
    if (duplicated) {
      setDraftErrors(
        Object.fromEntries(LANGUAGES.map((language) => [language, message("errors.optionValueDuplicated")]))
      );
      return;
    }
    setDraftErrors({});
    onChange(
      editedCode === null
        ? [
            ...values,
            {
              code: uniqueCode(
                label,
                values.map((value) => value.code)
              ),
              label,
            },
          ]
        : values.map((existing) => (existing.code === editedCode ? { ...existing, label } : existing))
    );
    setDraft(emptyLocalizedText());
    setEditedCode(null);
  }

  return (
    <>
      <div
        onKeyDown={(event) => {
          if (event.key === "Enter") {
            event.preventDefault();
            commit();
          }
        }}
      >
        <LocalizedTextField
          label={t("value")}
          value={draft}
          placeholder={t("valuePlaceholder")}
          onChange={setDraft}
          errors={draftErrors}
        />
      </div>
      <Button onClick={commit}>{editedCode === null ? t("addValue") : t("updateValue")}</Button>
      <ErrorMessage message={error} />
      {values.length > 0 && (
        <table className="primary-table mt-3">
          <thead>
            <tr>
              <th>{t("value")}</th>
              <th>{t("code")}</th>
              <th>{tActions("header")}</th>
            </tr>
          </thead>
          <tbody>
            {values.map((value) => (
              <tr key={value.code}>
                <td data-label={t("value")}>{localized(value.label)}</td>
                <td data-label={t("code")}>
                  <code>{value.code}</code>
                </td>
                <td data-label={tActions("header")}>
                  <Button
                    className="mr-2"
                    onClick={() => {
                      setDraft(value.label);
                      setEditedCode(value.code);
                    }}
                  >
                    {tActions("edit")}
                  </Button>
                  <Button
                    variant="danger"
                    onClick={() => onChange(values.filter((existing) => existing.code !== value.code))}
                  >
                    {tActions("delete")}
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </>
  );
}
