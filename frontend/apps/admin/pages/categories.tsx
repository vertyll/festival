import { useTranslations } from "use-intl";
import type { Category, CategoryRequest } from "@festival/shared/api/types";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import { emptyLocalizedText } from "@festival/shared/i18n/localized";
import { LOCALIZED_NAME_RULES } from "@festival/shared/validation/validation";
import Field from "@/components/molecules/Field";
import LocalizedTextField from "@/components/molecules/LocalizedTextField";
import InlineCrudPage from "@/components/templates/InlineCrudPage";
import { admin } from "@/lib/api";
import { orNull } from "@/lib/useForm";

export default function CategoriesPage() {
  const t = useTranslations("admin.categories");
  const localized = useLocalized();
  return (
    <InlineCrudPage<Category, CategoryRequest>
      title={t("title")}
      api={admin.categories}
      emptyRequest={{ name: emptyLocalizedText(), parentId: null }}
      toRequest={(category) => ({ name: category.name, parentId: category.parent?.id ?? null })}
      rules={{ name: LOCALIZED_NAME_RULES }}
      label={(category) => localized(category.name)}
      createHeading={t("create")}
      columns={[
        { header: t("name"), render: (category) => localized(category.name) },
        { header: t("parent"), render: (category) => category.parent && localized(category.parent.name) },
      ]}
      renderFields={(form, categories, edited) => (
        <>
          <LocalizedTextField
            label={t("name")}
            value={form.values.name}
            placeholder={t("namePlaceholder")}
            onChange={(name) => form.set("name", name)}
            errors={form.localizedErrors("name")}
          />
          <Field label={t("parent")} error={form.error("parentId")}>
            <select
              value={form.values.parentId ?? ""}
              onChange={(event) => form.set("parentId", orNull(event.target.value))}
            >
              <option value="">{t("noParent")}</option>
              {categories
                ?.filter((category) => category.id !== edited?.id)
                .map((category) => (
                  <option key={category.id} value={category.id}>
                    {localized(category.name)}
                  </option>
                ))}
            </select>
          </Field>
        </>
      )}
    />
  );
}
