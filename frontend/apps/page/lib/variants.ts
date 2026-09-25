import type { ProductDetails, SelectedOption, SelectedOptions, VariantAvailability } from "@festival/shared/api/types";

function matches(variant: VariantAvailability, product: ProductDetails, selected: SelectedOptions): boolean {
  return product.options.every((option, index) => {
    const valueCode = selected[option.code];
    return valueCode === undefined || variant.valueCodes[index] === valueCode;
  });
}

export function isSelectable(
  product: ProductDetails,
  selected: SelectedOptions,
  optionCode: string,
  valueCode: string
): boolean {
  const next = { ...selected, [optionCode]: valueCode };
  return product.variants.some((variant) => variant.available && matches(variant, product, next));
}

export function selectedVariant(product: ProductDetails, selected: SelectedOptions): VariantAvailability | null {
  const complete = product.options.every((option) => selected[option.code] !== undefined);
  return complete ? (product.variants.find((variant) => matches(variant, product, selected)) ?? null) : null;
}

export function variantSelection(product: ProductDetails, valueCodes: readonly string[]): SelectedOption[] {
  return product.options.map((option, index) => {
    const value = option.values.find((candidate) => candidate.code === valueCodes[index]);
    if (!value) {
      throw new Error(`Produkt ${product.id} nie ma wartości ${valueCodes[index]} opcji ${option.code}`);
    }
    return { option: option.name, value: value.label };
  });
}
