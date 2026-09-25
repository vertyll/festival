import { useTranslations } from "use-intl";
import type { News, NewsRequest } from "@festival/shared/api/types";
import { emptyLocalizedText } from "@festival/shared/i18n/localized";
import { IMAGES_RULES, LIMITS, LOCALIZED_NAME_RULES, localizedText } from "@festival/shared/validation/validation";
import LocalizedTextField from "@/components/molecules/LocalizedTextField";
import EditorForm from "@/components/templates/EditorForm";
import { admin } from "@/lib/api";
import { useForm } from "@/lib/useForm";
import ImagesField from "../ImagesField";

const EMPTY: NewsRequest = { name: emptyLocalizedText(), description: emptyLocalizedText(), images: [] };

export default function NewsForm({ news }: Readonly<{ news: News | null }>) {
  const t = useTranslations("admin.news");
  const form = useForm<NewsRequest>(
    news ? { name: news.name, description: news.description, images: news.images } : EMPTY,
    {
      name: LOCALIZED_NAME_RULES,
      description: [localizedText(LIMITS.descriptionMaxLength)],
      images: IMAGES_RULES,
    }
  );

  return (
    <EditorForm
      listPath="/news"
      submitting={form.submitting}
      onSave={() => form.submit((request) => (news ? admin.news.update(news.id, request) : admin.news.create(request)))}
    >
      <LocalizedTextField
        label={t("name")}
        value={form.values.name}
        placeholder={t("namePlaceholder")}
        onChange={(name) => form.set("name", name)}
        errors={form.localizedErrors("name")}
      />
      <ImagesField
        images={form.values.images}
        onChange={(images) => form.set("images", images)}
        error={form.error("images")}
      />
      <LocalizedTextField
        label={t("description")}
        value={form.values.description}
        placeholder={t("descriptionPlaceholder")}
        rows={10}
        onChange={(description) => form.set("description", description)}
        errors={form.localizedErrors("description")}
      />
    </EditorForm>
  );
}
