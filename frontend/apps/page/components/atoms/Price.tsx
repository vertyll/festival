import { useFormatter } from "use-intl";
import { useShopSettings } from "@/lib/shopSettings";
import Spinner from "./Spinner";

export default function Price({ amount }: Readonly<{ amount: number }>) {
  const settings = useShopSettings();
  const format = useFormatter();
  if (!settings) {
    return <Spinner size="1em" borderWidth="0.2em" padding="0" />;
  }
  return <>{format.number(amount, { style: "currency", currency: settings.currency })}</>;
}
