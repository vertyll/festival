import { useTranslations } from "use-intl";
import { LANGUAGES, type LocalizedText } from "@festival/shared/api/types";
import { withLanguage } from "@festival/shared/i18n/localized";
import type { LocalizedErrors } from "@/lib/useForm";
import ErrorMessage from "../atoms/ErrorMessage";

interface LocalizedTextFieldProps {
  label: string;
  value: LocalizedText;
  onChange: (value: LocalizedText) => void;
  errors: LocalizedErrors;
  placeholder?: string;
  rows?: number;
  sideBySide?: boolean;
}

export default function LocalizedTextField({
  label,
  value,
  onChange,
  errors,
  placeholder,
  rows,
  sideBySide = false,
}: Readonly<LocalizedTextFieldProps>) {
  const t = useTranslations("common.language");
  return (
    <fieldset className={`mb-2 ${sideBySide ? "grid md:grid-cols-2 gap-x-2" : ""}`}>
      <legend className={sideBySide ? "sr-only" : ""}>{label}</legend>
      {LANGUAGES.map((language) => {
        const inputProps = {
          lang: language,
          value: value[language],
          placeholder,
          onChange: (event: { target: { value: string } }) =>
            onChange(withLanguage(value, language, event.target.value)),
        };
        return (
          <div key={language}>
            <label>
              <span className="text-xs uppercase text-neutral-500">{t(`name.${language}`)}</span>
              {rows ? <textarea rows={rows} {...inputProps} /> : <input {...inputProps} />}
            </label>
            <ErrorMessage message={errors[language]} />
          </div>
        );
      })}
    </fieldset>
  );
}
