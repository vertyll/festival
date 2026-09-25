import { useTranslations } from "use-intl";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import ProductForm from "@/components/organisms/forms/ProductForm";
import ResourceEditorPage from "@/components/templates/ResourceEditorPage";
import { admin } from "@/lib/api";

export default function EditProductPage() {
  const t = useTranslations("admin.products");
  const localized = useLocalized();
  return (
    <ResourceEditorPage
      title={(product) => t("editTitle", { name: localized(product.name) })}
      load={admin.products.get}
    >
      {(product) => <ProductForm product={product} />}
    </ResourceEditorPage>
  );
}
