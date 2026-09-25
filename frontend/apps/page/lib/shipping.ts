import { useEffect, useState } from "react";
import type { ShippingDetails } from "@festival/shared/api/types";
import {
  LIMITS,
  email,
  hasErrors,
  maxLength,
  required,
  validate,
  type FieldErrors,
  type Rules,
} from "@festival/shared/validation/validation";
import { account } from "./api";

export const EMPTY_SHIPPING: ShippingDetails = {
  name: "",
  email: "",
  streetAddress: "",
  postalCode: "",
  city: "",
  country: "",
};

export const SHIPPING_RULES: Rules<ShippingDetails> = {
  name: [required(), maxLength(LIMITS.nameMaxLength)],
  email: [required(), email()],
  streetAddress: [required(), maxLength(LIMITS.streetAddressMaxLength)],
  postalCode: [required(), maxLength(LIMITS.postalCodeMaxLength)],
  city: [required(), maxLength(LIMITS.nameMaxLength)],
  country: [required(), maxLength(LIMITS.nameMaxLength)],
};

export interface ShippingForm {
  value: ShippingDetails;
  errors: FieldErrors;
  status: "loading" | "loaded" | "failed";
  onChange: (value: ShippingDetails) => void;
  setErrors: (errors: FieldErrors) => void;
  validate: () => boolean;
}

export function useShippingForm(authenticated: boolean): ShippingForm {
  const [value, setValue] = useState<ShippingDetails>(EMPTY_SHIPPING);
  const [errors, setErrors] = useState<FieldErrors>({});
  const [status, setStatus] = useState<ShippingForm["status"]>("loading");

  useEffect(() => {
    if (!authenticated) {
      return undefined;
    }
    let active = true;
    account.address().then(
      (saved) => {
        if (active) {
          setValue(saved ?? EMPTY_SHIPPING);
          setStatus("loaded");
        }
      },
      () => {
        if (active) {
          setStatus("failed");
        }
      }
    );
    return () => {
      active = false;
    };
  }, [authenticated]);

  return {
    value,
    errors,
    status,
    onChange: setValue,
    setErrors,
    validate: () => {
      const found = validate(value, SHIPPING_RULES);
      setErrors(found);
      return !hasErrors(found);
    },
  };
}

export function shippingErrorsOf(fieldErrors: FieldErrors): FieldErrors {
  const prefix = "shipping.";
  return Object.fromEntries(
    Object.entries(fieldErrors)
      .filter(([field]) => field.startsWith(prefix))
      .map(([field, error]) => [field.slice(prefix.length), error])
  );
}
