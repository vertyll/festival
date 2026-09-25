import { useTranslations } from "use-intl";
import type { Sponsor, SponsorRequest } from "@festival/shared/api/types";
import { IMAGES_RULES, NAME_RULES, httpUrl } from "@festival/shared/validation/validation";
import Field from "@/components/molecules/Field";
import EditorForm from "@/components/templates/EditorForm";
import { admin } from "@/lib/api";
import { useForm } from "@/lib/useForm";
import ImagesField from "../ImagesField";

const EMPTY: SponsorRequest = { name: "", link: "", images: [] };

export default function SponsorForm({ sponsor }: Readonly<{ sponsor: Sponsor | null }>) {
  const t = useTranslations("admin.sponsors");
  const form = useForm<SponsorRequest>(
    sponsor ? { name: sponsor.name, link: sponsor.link, images: sponsor.images } : EMPTY,
    { name: NAME_RULES, link: [httpUrl()], images: IMAGES_RULES }
  );

  return (
    <EditorForm
      listPath="/sponsors"
      submitting={form.submitting}
      onSave={() =>
        form.submit((request) =>
          sponsor ? admin.sponsors.update(sponsor.id, request) : admin.sponsors.create(request)
        )
      }
    >
      <Field label={t("name")} error={form.error("name")}>
        <input
          value={form.values.name}
          placeholder={t("namePlaceholder")}
          onChange={(event) => form.set("name", event.target.value)}
        />
      </Field>
      <ImagesField
        images={form.values.images}
        onChange={(images) => form.set("images", images)}
        error={form.error("images")}
      />
      <Field label={t("link")} error={form.error("link")}>
        <input
          type="url"
          value={form.values.link}
          placeholder={t("linkPlaceholder")}
          onChange={(event) => form.set("link", event.target.value)}
        />
      </Field>
    </EditorForm>
  );
}
