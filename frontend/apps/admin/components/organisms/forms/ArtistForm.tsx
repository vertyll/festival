import { useTranslations } from "use-intl";
import type { Artist, ArtistRequest, LocalizedText } from "@festival/shared/api/types";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import { emptyLocalizedText, isBlankLocalizedText } from "@festival/shared/i18n/localized";
import { useLoader } from "@festival/shared/react/useLoader";
import { IMAGES_RULES, LIMITS, NAME_RULES, optionalLocalizedText } from "@festival/shared/validation/validation";
import Field from "@/components/molecules/Field";
import LocalizedTextField from "@/components/molecules/LocalizedTextField";
import EditorForm from "@/components/templates/EditorForm";
import { admin } from "@/lib/api";
import { orNull, useForm } from "@/lib/useForm";
import ImagesField from "../ImagesField";

type ArtistFormValues = Omit<ArtistRequest, "description"> & { description: LocalizedText };

const EMPTY: ArtistFormValues = {
  name: "",
  description: emptyLocalizedText(),
  images: [],
  stageId: null,
  concertDate: null,
  concertTime: null,
};

function toValues(artist: Artist): ArtistFormValues {
  const { name, description, images, stageId, concertDate, concertTime } = artist;
  return { name, description: description ?? emptyLocalizedText(), images, stageId, concertDate, concertTime };
}

function toRequest(values: ArtistFormValues): ArtistRequest {
  return { ...values, description: isBlankLocalizedText(values.description) ? null : values.description };
}

export default function ArtistForm({ artist }: Readonly<{ artist: Artist | null }>) {
  const t = useTranslations("admin.artists");
  const localized = useLocalized();
  const stages = useLoader(admin.stages.list);
  const form = useForm<ArtistFormValues>(artist ? toValues(artist) : EMPTY, {
    name: NAME_RULES,
    description: [optionalLocalizedText(LIMITS.descriptionMaxLength)],
    images: IMAGES_RULES,
  });

  return (
    <EditorForm
      listPath="/artists"
      submitting={form.submitting}
      onSave={() =>
        form.submit((values) => {
          const request = toRequest(values);
          return artist ? admin.artists.update(artist.id, request) : admin.artists.create(request);
        })
      }
    >
      <Field label={t("name")} error={form.error("name")}>
        <input
          value={form.values.name}
          placeholder={t("namePlaceholder")}
          onChange={(event) => form.set("name", event.target.value)}
        />
      </Field>
      <Field label={t("stage")} error={form.error("stageId")}>
        <select value={form.values.stageId ?? ""} onChange={(event) => form.set("stageId", orNull(event.target.value))}>
          <option value="">{t("noStage")}</option>
          {stages.data?.map((stage) => (
            <option key={stage.id} value={stage.id}>
              {localized(stage.name)}
            </option>
          ))}
        </select>
      </Field>
      <Field label={t("concertDate")} error={form.error("concertDate")}>
        <input
          type="date"
          value={form.values.concertDate ?? ""}
          onChange={(event) => form.set("concertDate", orNull(event.target.value))}
        />
      </Field>
      <Field label={t("concertTime")} error={form.error("concertTime")}>
        <input
          type="time"
          value={form.values.concertTime ?? ""}
          onChange={(event) => form.set("concertTime", orNull(event.target.value))}
        />
      </Field>
      <ImagesField
        images={form.values.images}
        onChange={(images) => form.set("images", images)}
        error={form.error("images")}
      />
      <LocalizedTextField
        label={t("description")}
        value={form.values.description}
        placeholder={t("descriptionPlaceholder")}
        rows={8}
        onChange={(description) => form.set("description", description)}
        errors={form.localizedErrors("description")}
      />
    </EditorForm>
  );
}
