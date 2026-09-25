import { useEffect, useRef, type ReactNode } from "react";

export type ModalTone = "success" | "error" | "warning";

const TONE_STYLES: Record<ModalTone, { badge: string; symbol: string }> = {
  success: { badge: "bg-green-100 text-green-600", symbol: "✓" },
  error: { badge: "bg-red-100 text-red-600", symbol: "✕" },
  warning: { badge: "bg-amber-100 text-amber-600", symbol: "!" },
};

interface ModalProps {
  tone: ModalTone;
  title: string;
  message: string | undefined;
  onClose: () => void;
  actions: ReactNode;
}

export default function Modal({ tone, title, message, onClose, actions }: Readonly<ModalProps>) {
  const dialog = useRef<HTMLDialogElement>(null);

  useEffect(() => {
    const element = dialog.current;
    element?.showModal();
    return () => element?.close();
  }, []);

  const style = TONE_STYLES[tone];
  return (
    <dialog
      ref={dialog}
      onCancel={(event) => {
        event.preventDefault();
        onClose();
      }}
      aria-labelledby="modal-title"
      className="m-auto w-full max-w-md rounded-lg p-6 text-center shadow-xl backdrop:bg-black/50"
    >
      <div
        className={`mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full text-2xl font-bold ${style.badge}`}
        aria-hidden="true"
      >
        {style.symbol}
      </div>
      <h2 id="modal-title" className="text-lg font-semibold text-neutral-800">
        {title}
      </h2>
      {message && <p className="mt-2 text-sm text-neutral-600">{message}</p>}
      <div className="mt-6 flex justify-center gap-2">{actions}</div>
    </dialog>
  );
}
