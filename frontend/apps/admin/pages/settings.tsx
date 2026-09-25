import type { ReactNode } from "react";
import { useTranslations } from "use-intl";
import type { ShopSettings } from "@festival/shared/api/types";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import { parseDecimal, shippingPrice } from "@festival/shared/validation/validation";
import Button from "@/components/atoms/Button";
import LoadFailed from "@/components/atoms/LoadFailed";
import Spinner from "@/components/atoms/Spinner";
import Field from "@/components/molecules/Field";
import Layout from "@/components/templates/Layout";
import { admin } from "@/lib/api";
import { useDialogs } from "@/lib/dialogs";
import { useForm } from "@/lib/useForm";

interface SettingsFormValues {
  shippingPrice: string;
  stockVisible: boolean;
  variantStockVisible: boolean;
}

function SettingsForm({ settings }: Readonly<{ settings: ShopSettings }>) {
  const t = useTranslations("admin.settings");
  const tActions = useTranslations("admin.actions");
  const { notifySuccess } = useDialogs();
  const form = useForm<SettingsFormValues>(
    {
      shippingPrice: settings.shippingPrice.toFixed(2),
      stockVisible: settings.stockVisible,
      variantStockVisible: settings.variantStockVisible,
    },
    { shippingPrice: [shippingPrice()] }
  );

  async function save() {
    const saved = await form.submit((values) =>
      admin.settings.update({
        shippingPrice: Number(parseDecimal(values.shippingPrice)),
        stockVisible: values.stockVisible,
        variantStockVisible: values.variantStockVisible,
      })
    );
    if (saved) {
      notifySuccess(t("saved"));
    }
  }

  return (
    <>
      <Field label={t("shippingPrice", { currency: settings.currency })} error={form.error("shippingPrice")}>
        <input
          inputMode="decimal"
          value={form.values.shippingPrice}
          onChange={(event) => form.set("shippingPrice", event.target.value)}
        />
      </Field>
      <label className="flex items-center gap-2 my-2">
        <input
          type="checkbox"
          className="w-auto mt-0"
          checked={form.values.stockVisible}
          onChange={(event) => form.set("stockVisible", event.target.checked)}
        />
        {t("stockVisible")}
      </label>
      <label className="flex items-center gap-2 my-2">
        <input
          type="checkbox"
          className="w-auto mt-0"
          checked={form.values.variantStockVisible}
          onChange={(event) => form.set("variantStockVisible", event.target.checked)}
        />
        {t("variantStockVisible")}
      </label>
      <p className="text-sm text-neutral-500 my-2">
        {t.rich("checkout", {
          b: (chunks: ReactNode) => <b>{chunks}</b>,
          enabled: String(settings.checkoutEnabled),
        })}
      </p>
      <Button disabled={form.submitting} onClick={() => void save()}>
        {tActions("save")}
      </Button>
    </>
  );
}

export default function SettingsPage() {
  const t = useTranslations("admin.settings");
  const settings = useLoader(admin.settings.get);
  return (
    <Layout title={t("title")}>
      <LoadState state={settings.state} loading={<Spinner />} failed={<LoadFailed />}>
        {(data) => <SettingsForm settings={data} />}
      </LoadState>
    </Layout>
  );
}
