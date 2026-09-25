import type { ProductOption, ProductVariant, SelectedOption } from "@festival/shared/api/types";

export interface VariantDraft {
  valueCodes: string[];
  stock: string;
}

const KEY_SEPARATOR = "\u001f";

export function variantKey(valueCodes: readonly string[]): string {
  return valueCodes.join(KEY_SEPARATOR);
}

function combinations(options: readonly ProductOption[]): string[][] {
  return options.reduce<string[][]>(
    (partial, option) => partial.flatMap((prefix) => option.values.map((value) => [...prefix, value.code])),
    [[]]
  );
}

export function variantsFor(options: readonly ProductOption[], previous: readonly VariantDraft[]): VariantDraft[] {
  const stocks = new Map(previous.map((variant) => [variantKey(variant.valueCodes), variant.stock]));
  return combinations(options).map((valueCodes) => ({
    valueCodes,
    stock: stocks.get(variantKey(valueCodes)) ?? "0",
  }));
}

export function variantSelection(options: readonly ProductOption[], valueCodes: readonly string[]): SelectedOption[] {
  return options.map((option, index) => {
    const value = option.values.find((candidate) => candidate.code === valueCodes[index]);
    if (!value) {
      throw new Error(`Opcja ${option.code} nie ma wartości ${valueCodes[index]}`);
    }
    return { option: option.name, value: value.label };
  });
}

export function toDrafts(variants: readonly ProductVariant[]): VariantDraft[] {
  return variants.map((variant) => ({ valueCodes: variant.valueCodes, stock: String(variant.stock) }));
}

export function toVariants(drafts: readonly VariantDraft[]): ProductVariant[] {
  return drafts.map((draft) => ({ valueCodes: draft.valueCodes, stock: Number(draft.stock) }));
}
