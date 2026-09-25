import { useTranslations } from "use-intl";
import type { Stage, StageRequest } from "@festival/shared/api/types";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import { emptyLocalizedText } from "@festival/shared/i18n/localized";
import { LOCALIZED_NAME_RULES } from "@festival/shared/validation/validation";
import LocalizedTextField from "@/components/molecules/LocalizedTextField";
import InlineCrudPage from "@/components/templates/InlineCrudPage";
import { admin } from "@/lib/api";

export default function StagesPage() {
  const t = useTranslations("admin.stages");
  const localized = useLocalized();
  return (
    <InlineCrudPage<Stage, StageRequest>
      title={t("title")}
      api={admin.stages}
      emptyRequest={{ name: emptyLocalizedText() }}
      toRequest={(stage) => ({ name: stage.name })}
      rules={{ name: LOCALIZED_NAME_RULES }}
      label={(stage) => localized(stage.name)}
      createHeading={t("create")}
      columns={[{ header: t("name"), render: (stage) => localized(stage.name) }]}
      renderFields={(form) => (
        <LocalizedTextField
          label={t("name")}
          value={form.values.name}
          placeholder={t("namePlaceholder")}
          onChange={(name) => form.set("name", name)}
          errors={form.localizedErrors("name")}
        />
      )}
    />
  );
}
