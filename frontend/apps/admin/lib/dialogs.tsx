import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from "react";
import { useTranslations } from "use-intl";
import { ApiError } from "@festival/shared/api/http";
import { useMessage } from "@festival/shared/i18n/IntlSetup";
import Button from "@/components/atoms/Button";
import Modal, { type ModalTone } from "@/components/organisms/Modal";

type OpenDialog =
  | { kind: "confirm"; title: string; message: string; resolve: (confirmed: boolean) => void }
  | { kind: "notice"; tone: ModalTone; title: string; message: string | undefined };

interface Dialogs {
  confirmDelete: (label: string) => Promise<boolean>;
  notifySuccess: (title: string, message?: string) => void;
  notifyError: (error: unknown) => void;
}

const DialogsContext = createContext<Dialogs | null>(null);

export function DialogProvider({ children }: Readonly<{ children: ReactNode }>) {
  const t = useTranslations("admin.dialogs");
  const describe = useMessage();
  const [dialog, setDialog] = useState<OpenDialog | null>(null);

  const confirmDelete = useCallback(
    (label: string) =>
      new Promise<boolean>((resolve) =>
        setDialog({ kind: "confirm", title: t("warning"), message: t("confirmDelete", { label }), resolve })
      ),
    [t]
  );

  const notifySuccess = useCallback(
    (title: string, message?: string) => setDialog({ kind: "notice", tone: "success", title, message }),
    []
  );

  const notifyError = useCallback(
    (error: unknown) => {
      if (!(error instanceof ApiError)) {
        throw error;
      }
      setDialog({ kind: "notice", tone: "error", title: t("failed"), message: describe(error.userMessage) });
    },
    [t, describe]
  );

  const answer = (confirmed: boolean) => {
    if (dialog?.kind === "confirm") {
      dialog.resolve(confirmed);
    }
    setDialog(null);
  };

  const value = useMemo(
    () => ({ confirmDelete, notifySuccess, notifyError }),
    [confirmDelete, notifySuccess, notifyError]
  );

  return (
    <DialogsContext.Provider value={value}>
      {children}
      {dialog?.kind === "confirm" && (
        <Modal
          tone="warning"
          title={dialog.title}
          message={dialog.message}
          onClose={() => answer(false)}
          actions={
            <>
              <Button variant="danger" onClick={() => answer(true)}>
                {t("yes")}
              </Button>
              <Button autoFocus onClick={() => answer(false)}>
                {t("no")}
              </Button>
            </>
          }
        />
      )}
      {dialog?.kind === "notice" && (
        <Modal
          tone={dialog.tone}
          title={dialog.title}
          message={dialog.message}
          onClose={() => answer(false)}
          actions={
            <Button autoFocus onClick={() => answer(false)}>
              {t("ok")}
            </Button>
          }
        />
      )}
    </DialogsContext.Provider>
  );
}

export function useConfirmedRemoval(): (label: string, remove: () => Promise<void>) => Promise<boolean> {
  const t = useTranslations("admin.dialogs");
  const { confirmDelete, notifyError, notifySuccess } = useDialogs();
  return async (label, remove) => {
    if (!(await confirmDelete(label))) {
      return false;
    }
    try {
      await remove();
      notifySuccess(t("deleted"), t("deletedItem", { label }));
      return true;
    } catch (error) {
      notifyError(error);
      return false;
    }
  };
}

export function useDialogs(): Dialogs {
  const value = useContext(DialogsContext);
  if (!value) {
    throw new Error("useDialogs() must be used within <DialogProvider>");
  }
  return value;
}
