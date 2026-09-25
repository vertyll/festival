import type { InputHTMLAttributes } from "react";
import type { Message } from "@festival/shared/api/types";
import { useMessage } from "@festival/shared/i18n/IntlSetup";
import ErrorDiv from "../atoms/ErrorDiv";
import Input from "../atoms/Input";
import Label from "../atoms/Label";

interface FieldInputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error: Message | undefined;
}

export default function FieldInput({ label, error, ...inputProps }: Readonly<FieldInputProps>) {
  const describe = useMessage();
  return (
    <div>
      <Label>
        {label}
        <Input {...inputProps} aria-invalid={error !== undefined} />
      </Label>
      {error && <ErrorDiv>{describe(error)}</ErrorDiv>}
    </div>
  );
}
