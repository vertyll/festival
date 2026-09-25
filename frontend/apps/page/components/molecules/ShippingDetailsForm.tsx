import type { ShippingDetails } from "@festival/shared/api/types";
import type { FieldErrors } from "@festival/shared/validation/validation";
import FieldInput from "./FieldInput";
import { useTranslations } from "use-intl";

const FIELDS: readonly { name: keyof ShippingDetails; type: string; autoComplete: string }[] = [
  { name: "name", type: "text", autoComplete: "name" },
  { name: "email", type: "email", autoComplete: "email" },
  { name: "streetAddress", type: "text", autoComplete: "street-address" },
  { name: "postalCode", type: "text", autoComplete: "postal-code" },
  { name: "city", type: "text", autoComplete: "address-level2" },
  { name: "country", type: "text", autoComplete: "country-name" },
];

interface ShippingDetailsFormProps {
  value: ShippingDetails;
  errors: FieldErrors;
  onChange: (value: ShippingDetails) => void;
}

export default function ShippingDetailsForm({ value, errors, onChange }: Readonly<ShippingDetailsFormProps>) {
  const t = useTranslations("page.shipping");
  return (
    <>
      {FIELDS.map((field) => (
        <FieldInput
          key={field.name}
          label={t(field.name)}
          type={field.type}
          autoComplete={field.autoComplete}
          value={value[field.name]}
          error={errors[field.name]}
          onChange={(event) => onChange({ ...value, [field.name]: event.target.value })}
        />
      ))}
    </>
  );
}
