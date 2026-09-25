import { useState } from "react";
import { ApiError } from "@festival/shared/api/http";
import { LANGUAGES, type Language, type Message } from "@festival/shared/api/types";
import { hasErrors, validate, type FieldErrors, type Rules } from "@festival/shared/validation/validation";
import { useDialogs } from "./dialogs";

export type LocalizedErrors = Partial<Record<Language, Message>>;

export interface Form<V> {
  values: V;
  submitting: boolean;
  error: (path: string) => Message | undefined;
  localizedErrors: (path: string) => LocalizedErrors;
  set: <K extends keyof V>(field: K, value: V[K]) => void;
  reset: (values: V) => void;
  submit: (action: (values: V) => Promise<unknown>) => Promise<boolean>;
}

function isWithin(key: string, path: string): boolean {
  return key === path || key.startsWith(`${path}.`) || key.startsWith(`${path}[`);
}

export function useForm<V extends object>(initial: V, rules: Rules<V>): Form<V> {
  const [values, setValues] = useState(initial);
  const [errors, setErrors] = useState<FieldErrors>({});
  const [submitting, setSubmitting] = useState(false);
  const { notifyError } = useDialogs();

  async function submit(action: (values: V) => Promise<unknown>): Promise<boolean> {
    const found = validate(values, rules);
    setErrors(found);
    if (hasErrors(found)) {
      return false;
    }
    setSubmitting(true);
    try {
      await action(values);
      return true;
    } catch (error) {
      if (error instanceof ApiError && error.status === 400 && hasErrors(error.fieldErrors)) {
        setErrors(error.fieldErrors);
      } else {
        notifyError(error);
      }
      return false;
    } finally {
      setSubmitting(false);
    }
  }

  function error(path: string): Message | undefined {
    return errors[path] ?? Object.entries(errors).find(([key]) => isWithin(key, path))?.[1];
  }

  return {
    values,
    submitting,
    error,
    localizedErrors: (path) =>
      Object.fromEntries(LANGUAGES.map((language) => [language, errors[path] ?? error(`${path}.${language}`)])),
    set: (field, value) => setValues((current) => ({ ...current, [field]: value })),
    reset: (next) => {
      setValues(next);
      setErrors({});
    },
    submit,
  };
}

export function orNull(value: string): string | null {
  return value === "" ? null : value;
}
