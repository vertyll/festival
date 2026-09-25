import { useTranslations } from "use-intl";
import type { AdminProduct, LocalizedText, ProductOption, ProductRequest } from "@festival/shared/api/types";
import { useDescribeSelection, useLocalized } from "@festival/shared/i18n/IntlSetup";
import { emptyLocalizedText, isBlankLocalizedText } from "@festival/shared/i18n/localized";
import { useLoader } from "@festival/shared/react/useLoader";
import {
  IMAGES_RULES,
  LIMITS,
  LOCALIZED_NAME_RULES,
  message,
  optionalLocalizedText,
  parseDecimal,
  price,
  stock,
} from "@festival/shared/validation/validation";
import ErrorMessage from "@/components/atoms/ErrorMessage";
import Field from "@/components/molecules/Field";
import LocalizedTextField from "@/components/molecules/LocalizedTextField";
import EditorForm from "@/components/templates/EditorForm";
import { admin } from "@/lib/api";
import {
  toDrafts,
  toVariants,
  variantKey,
  variantSelection,
  variantsFor,
  type VariantDraft,
} from "@/lib/productVariants";
import { orNull, useForm } from "@/lib/useForm";
import ImagesField from "../ImagesField";
import ProductOptionsEditor from "./ProductOptionsEditor";

interface ProductFormValues {
  name: LocalizedText;
  description: LocalizedText;
  price: string;
  categoryId: string | null;
  images: string[];
  options: ProductOption[];
  variants: VariantDraft[];
}

const EMPTY: ProductFormValues = {
  name: emptyLocalizedText(),
  description: emptyLocalizedText(),
  price: "",
  categoryId: null,
  images: [],
  options: [],
  variants: variantsFor([], []),
};

function toValues(product: AdminProduct): ProductFormValues {
  return {
    name: product.name,
    description: product.description ?? emptyLocalizedText(),
    price: product.price.toFixed(2),
    categoryId: product.categoryId,
    images: product.images,
    options: product.options,
    variants: toDrafts(product.variants),
  };
}

function toRequest(values: ProductFormValues): ProductRequest {
  return {
    name: values.name,
    description: isBlankLocalizedText(values.description) ? null : values.description,
    price: Number(parseDecimal(values.price)),
    categoryId: values.categoryId,
    images: values.images,
    options: values.options,
    variants: toVariants(values.variants),
  };
}

const validStock = stock();

export default function ProductForm({ product }: Readonly<{ product: AdminProduct | null }>) {
  const t = useTranslations("admin.products");
  const localized = useLocalized();
  const describeSelection = useDescribeSelection();
  const categories = useLoader(admin.categories.list);
  const attributes = useLoader(admin.attributes.list);
  const form = useForm<ProductFormValues>(product ? toValues(product) : EMPTY, {
    name: LOCALIZED_NAME_RULES,
    description: [optionalLocalizedText(LIMITS.descriptionMaxLength)],
    price: [price()],
    images: IMAGES_RULES,
    options: [
      (options) =>
        options.some((option) => option.values.length === 0) ? message("validation.optionValuesRequired") : null,
      (options) =>
        options.some((option) => option.values.length > LIMITS.maxOptionValues)
          ? message("validation.tooManyOptionValues", { max: LIMITS.maxOptionValues })
          : null,
    ],
    variants: [(variants) => variants.map((variant) => validStock(variant.stock)).find(Boolean) ?? null],
  });
  const { values } = form;

  function changeOptions(options: ProductOption[]) {
    form.set("options", options);
    form.set("variants", variantsFor(options, values.variants));
  }

  function changeStock(key: string, stockText: string) {
    form.set(
      "variants",
      values.variants.map((variant) =>
        variantKey(variant.valueCodes) === key ? { ...variant, stock: stockText } : variant
      )
    );
  }

  return (
    <EditorForm
      listPath="/products"
      submitting={form.submitting}
      onSave={() =>
        form.submit((current) => {
          const request = toRequest(current);
          return product ? admin.products.update(product.id, request) : admin.products.create(request);
        })
      }
    >
      <LocalizedTextField
        label={t("name")}
        value={values.name}
        placeholder={t("namePlaceholder")}
        onChange={(name) => form.set("name", name)}
        errors={form.localizedErrors("name")}
      />
      <Field label={t("category")} error={form.error("categoryId")}>
        <select
          value={values.categoryId ?? ""}
          onChange={(event) => form.set("categoryId", orNull(event.target.value))}
        >
          <option value="">{t("noCategory")}</option>
          {categories.data?.map((category) => (
            <option key={category.id} value={category.id}>
              {category.parent
                ? t("categoryPath", { parent: localized(category.parent.name), name: localized(category.name) })
                : localized(category.name)}
            </option>
          ))}
        </select>
      </Field>
      <ImagesField
        images={values.images}
        onChange={(images) => form.set("images", images)}
        error={form.error("images")}
      />
      <LocalizedTextField
        label={t("description")}
        value={values.description}
        placeholder={t("descriptionPlaceholder")}
        rows={6}
        onChange={(description) => form.set("description", description)}
        errors={form.localizedErrors("description")}
      />
      <Field label={t("price")} error={form.error("price")}>
        <input
          inputMode="decimal"
          value={values.price}
          placeholder={t("pricePlaceholder")}
          onChange={(event) => form.set("price", event.target.value)}
        />
      </Field>
      <ProductOptionsEditor options={values.options} attributes={attributes.data ?? []} onChange={changeOptions} />
      <div className="mb-2">
        <span className="block text-sm font-medium text-neutral-800">{t("stock")}</span>
        {values.variants.map((variant) => {
          const key = variantKey(variant.valueCodes);
          return (
            <label key={key} className="flex items-center gap-2">
              <span className="min-w-40">
                {variant.valueCodes.length > 0
                  ? describeSelection(variantSelection(values.options, variant.valueCodes))
                  : t("pieces")}
              </span>
              <input
                type="number"
                min={0}
                step={1}
                value={variant.stock}
                onChange={(event) => changeStock(key, event.target.value)}
              />
            </label>
          );
        })}
        {values.variants.length === 0 && <p className="text-sm">{t("selectOptionValues")}</p>}
        <ErrorMessage message={form.error("options")} />
        <ErrorMessage message={form.error("variants")} />
      </div>
    </EditorForm>
  );
}
