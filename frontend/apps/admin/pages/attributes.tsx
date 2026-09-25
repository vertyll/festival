import { useTranslations } from "use-intl";
import type { Attribute, AttributeRequest } from "@festival/shared/api/types";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import { emptyLocalizedText } from "@festival/shared/i18n/localized";
import { LIMITS, LOCALIZED_NAME_RULES, maxItems, message } from "@festival/shared/validation/validation";
import LocalizedTextField from "@/components/molecules/LocalizedTextField";
import ValueListEditor from "@/components/molecules/ValueListEditor";
import InlineCrudPage from "@/components/templates/InlineCrudPage";
import { admin } from "@/lib/api";

export default function AttributesPage() {
  const t = useTranslations("admin.attributes");
  const localized = useLocalized();
  return (
    <InlineCrudPage<Attribute, AttributeRequest>
      title={t("title")}
      api={admin.attributes}
      emptyRequest={{ name: emptyLocalizedText(), values: [] }}
      toRequest={(attribute) => ({ name: attribute.name, values: attribute.values })}
      rules={{
        name: LOCALIZED_NAME_RULES,
        values: [
          (values) => (values.length > 0 ? null : message("validation.optionValuesRequired")),
          maxItems(LIMITS.maxAttributeValues, "validation.tooManyAttributeValues"),
        ],
      }}
      label={(attribute) => localized(attribute.name)}
      createHeading={t("create")}
      columns={[
        { header: t("name"), render: (attribute) => localized(attribute.name) },
        {
          header: t("values"),
          render: (attribute) => attribute.values.map((value) => localized(value.label)).join(", "),
        },
      ]}
      renderFields={(form) => (
        <>
          <LocalizedTextField
            label={t("name")}
            value={form.values.name}
            placeholder={t("namePlaceholder")}
            onChange={(name) => form.set("name", name)}
            errors={form.localizedErrors("name")}
          />
          <ValueListEditor
            values={form.values.values}
            onChange={(values) => form.set("values", values)}
            error={form.error("values")}
          />
        </>
      )}
    />
  );
}
