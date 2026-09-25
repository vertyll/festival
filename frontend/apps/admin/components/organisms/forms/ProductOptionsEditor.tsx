import { useState } from "react";
import { useTranslations } from "use-intl";
import type { Attribute, OptionValue, ProductOption } from "@festival/shared/api/types";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import { LIMITS } from "@festival/shared/validation/validation";
import Button from "@/components/atoms/Button";

interface ProductOptionsEditorProps {
  options: readonly ProductOption[];
  attributes: readonly Attribute[];
  onChange: (options: ProductOption[]) => void;
}

function choicesFor(option: ProductOption, attributes: readonly Attribute[]): OptionValue[] {
  const attributeValues = attributes.find((attribute) => attribute.id === option.code)?.values ?? [];
  const retired = option.values.filter((value) => !attributeValues.some((candidate) => candidate.code === value.code));
  return [...attributeValues, ...retired];
}

export default function ProductOptionsEditor({ options, attributes, onChange }: Readonly<ProductOptionsEditorProps>) {
  const t = useTranslations("admin.products.options");
  const localized = useLocalized();
  const unused = attributes.filter((attribute) => !options.some((option) => option.code === attribute.id));
  const [selectedAttribute, setSelectedAttribute] = useState("");

  function addOption() {
    const attribute = unused.find((candidate) => candidate.id === selectedAttribute) ?? unused[0];
    if (attribute) {
      onChange([...options, { code: attribute.id, name: attribute.name, values: [] }]);
      setSelectedAttribute("");
    }
  }

  function toggleValue(option: ProductOption, value: OptionValue) {
    const choices = choicesFor(option, attributes);
    const selected = option.values.some((existing) => existing.code === value.code)
      ? option.values.filter((existing) => existing.code !== value.code)
      : [...option.values, value];
    const values = choices.filter((choice) => selected.some((existing) => existing.code === choice.code));
    onChange(options.map((existing) => (existing.code === option.code ? { ...option, values } : existing)));
  }

  return (
    <div className="mb-2">
      <span className="block text-sm font-medium text-neutral-800">{t("label")}</span>
      {options.map((option) => (
        <div key={option.code} className="border border-neutral-300 rounded-md p-2 my-2">
          <b>{localized(option.name)}</b>
          <div className="flex flex-wrap gap-2 my-2">
            {choicesFor(option, attributes).map((value) => (
              <label key={value.code} className="flex items-center gap-1">
                <input
                  type="checkbox"
                  className="w-auto mt-0"
                  checked={option.values.some((existing) => existing.code === value.code)}
                  onChange={() => toggleValue(option, value)}
                />
                {localized(value.label)}
              </label>
            ))}
          </div>
          {option.values.length === 0 && <div className="error-message">{t("valueRequired")}</div>}
          <Button variant="danger" onClick={() => onChange(options.filter((existing) => existing !== option))}>
            {t("remove")}
          </Button>
        </div>
      ))}
      {unused.length > 0 && options.length < LIMITS.maxProductOptions && (
        <div className="flex gap-2 items-center">
          <select
            value={selectedAttribute}
            onChange={(event) => setSelectedAttribute(event.target.value)}
            aria-label={t("attribute")}
          >
            {unused.map((attribute) => (
              <option key={attribute.id} value={attribute.id}>
                {localized(attribute.name)}
              </option>
            ))}
          </select>
          <Button onClick={addOption}>{t("add")}</Button>
        </div>
      )}
    </div>
  );
}
