import { useRouter } from "next/router";
import type { FormEvent, ReactNode } from "react";
import { useTranslations } from "use-intl";
import Button from "../atoms/Button";

interface EditorFormProps {
  onSave: () => Promise<boolean>;
  listPath: string;
  submitting: boolean;
  children: ReactNode;
}

export default function EditorForm({ onSave, listPath, submitting, children }: Readonly<EditorFormProps>) {
  const t = useTranslations("admin.actions");
  const router = useRouter();

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    if (await onSave()) {
      await router.push(listPath);
    }
  }

  return (
    <form onSubmit={(event) => void handleSubmit(event)} noValidate>
      {children}
      <div className="flex gap-1">
        <Button type="submit" disabled={submitting}>
          {t("save")}
        </Button>
        <Button variant="danger" onClick={() => void router.push(listPath)}>
          {t("cancel")}
        </Button>
      </div>
    </form>
  );
}
