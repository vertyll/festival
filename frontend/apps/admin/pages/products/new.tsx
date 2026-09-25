import { useTranslations } from "use-intl";
import ProductForm from "@/components/organisms/forms/ProductForm";
import Layout from "@/components/templates/Layout";

export default function NewProductPage() {
  const t = useTranslations("admin.products");
  return (
    <Layout title={t("newTitle")}>
      <ProductForm product={null} />
    </Layout>
  );
}
