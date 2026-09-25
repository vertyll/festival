import { useFormatter, useTranslations } from "use-intl";
import type { Administrator, AdministratorRequest } from "@festival/shared/api/types";
import { email, required } from "@festival/shared/validation/validation";
import Field from "@/components/molecules/Field";
import InlineCrudPage from "@/components/templates/InlineCrudPage";
import { admin } from "@/lib/api";

export default function AdministratorsPage() {
  const t = useTranslations("admin.administrators");
  const format = useFormatter();
  return (
    <InlineCrudPage<Administrator, AdministratorRequest>
      title={t("title")}
      api={admin.administrators}
      emptyRequest={{ email: "" }}
      toRequest={(administrator) => ({ email: administrator.email })}
      rules={{ email: [required(), email()] }}
      label={(administrator) => administrator.email}
      createHeading={t("create")}
      columns={[
        { header: t("email"), render: (administrator) => administrator.email },
        {
          header: t("createdAt"),
          render: (administrator) =>
            format.dateTime(new Date(administrator.createdAt), { dateStyle: "medium", timeStyle: "short" }),
        },
      ]}
      renderFields={(form) => (
        <Field label={t("email")} error={form.error("email")}>
          <input
            type="email"
            value={form.values.email}
            placeholder={t("emailPlaceholder")}
            onChange={(event) => form.set("email", event.target.value)}
          />
        </Field>
      )}
    />
  );
}
