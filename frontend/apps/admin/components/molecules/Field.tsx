import type { ReactNode } from "react";
import type { Message } from "@festival/shared/api/types";
import ErrorMessage from "../atoms/ErrorMessage";

interface FieldProps {
  label: string;
  error: Message | undefined;
  children: ReactNode;
}

export default function Field({ label, error, children }: Readonly<FieldProps>) {
  return (
    <div className="mb-2">
      <label>
        <span>{label}</span>
        {children}
      </label>
      <ErrorMessage message={error} />
    </div>
  );
}
