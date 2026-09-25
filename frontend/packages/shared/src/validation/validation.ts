import { ApiError } from "../api/http";
import { LANGUAGES, type Language, type LocalizedText, type Message, type MessageArgs } from "../api/types";
import { isBlankLocalizedText } from "../i18n/localized";

export const LIMITS = {
  nameMaxLength: 50,
  descriptionMaxLength: 10_000,
  emailMaxLength: 254,
  urlMaxLength: 2048,
  attributeValueMaxLength: 25,
  streetAddressMaxLength: 100,
  postalCodeMaxLength: 20,
  maxImages: 20,
  maxAttributeValues: 50,
  maxProductOptions: 5,
  maxOptionValues: 20,
  maxCartLines: 50,
  maxQuantity: 99,
  maxStock: 1_000_000,
  maxPrice: 99_999_999.99,
  maxShippingPrice: 999_999.99,
} as const;

export interface FieldMessage extends Message {
  language?: Language;
}

export type Rule<T> = (value: T) => FieldMessage | null;

export type Rules<V> = { [K in keyof V]?: readonly Rule<V[K]>[] };

export type FieldErrors = Readonly<Record<string, Message>>;

export function message(code: string, args: MessageArgs = {}): Message {
  return { code, args };
}

export function validate<V extends object>(values: V, rules: Rules<V>): FieldErrors {
  const errors: Record<string, Message> = {};
  for (const key of Object.keys(rules) as (keyof V & string)[]) {
    const error = rules[key]?.map((rule) => rule(values[key])).find((found) => found !== null);
    if (error) {
      errors[error.language ? `${key}.${error.language}` : key] = { code: error.code, args: error.args };
    }
  }
  return errors;
}

export function hasErrors(errors: FieldErrors): boolean {
  return Object.keys(errors).length > 0;
}

export function isValidationError(error: unknown): error is ApiError {
  return error instanceof ApiError && error.status === 400 && hasErrors(error.fieldErrors);
}

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const required = (): Rule<string> => (value) => (value.trim() ? null : message("validation.required"));

export const maxLength =
  (max: number): Rule<string> =>
  (value) =>
    value.trim().length > max ? message("validation.tooLong", { max }) : null;

export const email = (): Rule<string> => (value) =>
  EMAIL_PATTERN.test(value.trim()) && value.trim().length <= LIMITS.emailMaxLength ? null : message("validation.email");

export const httpUrl = (): Rule<string> => (value) => {
  if (value.trim().length > LIMITS.urlMaxLength) {
    return message("validation.url");
  }
  try {
    const url = new URL(value.trim());
    return (url.protocol === "http:" || url.protocol === "https:") && url.hostname !== ""
      ? null
      : message("validation.url");
  } catch {
    return message("validation.url");
  }
};

function checkLanguages(text: LocalizedText, max: number): FieldMessage | null {
  for (const language of LANGUAGES) {
    const value = text[language].trim();
    if (value === "") {
      return { ...message("validation.required"), language };
    }
    if (value.length > max) {
      return { ...message("validation.tooLong", { max }), language };
    }
  }
  return null;
}

export const localizedText =
  (max: number): Rule<LocalizedText> =>
  (value) =>
    checkLanguages(value, max);

export const optionalLocalizedText =
  (max: number): Rule<LocalizedText> =>
  (value) =>
    isBlankLocalizedText(value) ? null : checkLanguages(value, max);

export function parseDecimal(text: string): number | null {
  return text.trim() === "" ? null : Number(text.trim().replace(",", "."));
}

function hasAtMostTwoDecimals(value: number): boolean {
  return Math.abs(Math.round(value * 100) - value * 100) < 1e-6;
}

function tooLarge(value: number): Message {
  return message("validation.tooLarge", { value });
}

export const price = (): Rule<string> => (text) => {
  const value = parseDecimal(text);
  if (value === null || !Number.isFinite(value) || value <= 0 || !hasAtMostTwoDecimals(value)) {
    return message("validation.price");
  }
  return value > LIMITS.maxPrice ? tooLarge(LIMITS.maxPrice) : null;
};

export const shippingPrice = (): Rule<string> => (text) => {
  const value = parseDecimal(text);
  if (value === null || !Number.isFinite(value) || value < 0 || !hasAtMostTwoDecimals(value)) {
    return message("validation.shippingPrice");
  }
  return value > LIMITS.maxShippingPrice ? tooLarge(LIMITS.maxShippingPrice) : null;
};

export const stock = (): Rule<string> => (text) => {
  const value = parseDecimal(text);
  if (value === null || !Number.isInteger(value) || value < 0) {
    return message("validation.stock");
  }
  return value > LIMITS.maxStock ? tooLarge(LIMITS.maxStock) : null;
};

export const maxItems =
  (max: number, code: string): Rule<readonly unknown[]> =>
  (items) =>
    items.length > max ? message(code, { max }) : null;

export const IMAGES_RULES: readonly Rule<readonly string[]>[] = [
  maxItems(LIMITS.maxImages, "validation.tooManyImages"),
];

export const NAME_RULES: readonly Rule<string>[] = [required(), maxLength(LIMITS.nameMaxLength)];
export const LOCALIZED_NAME_RULES: readonly Rule<LocalizedText>[] = [localizedText(LIMITS.nameMaxLength)];
