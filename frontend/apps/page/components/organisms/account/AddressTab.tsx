import { useState } from "react";
import styled from "styled-components";
import { useTranslations } from "use-intl";
import { ApiError } from "@festival/shared/api/http";
import { useMessage } from "@festival/shared/i18n/IntlSetup";
import { isValidationError } from "@festival/shared/validation/validation";
import { Alert, type AlertType } from "@/components/atoms/Alert";
import Button from "@/components/atoms/Button";
import LoadFailed from "@/components/atoms/LoadFailed";
import ShippingDetailsForm from "@/components/molecules/ShippingDetailsForm";
import { account } from "@/lib/api";
import { useShippingForm } from "@/lib/shipping";
import AccountSpinner from "./AccountSpinner";
import { TabContent } from "./TabContent";

const AddressForm = styled(TabContent)`
  display: grid;

  & > div {
    max-width: 450px;
    width: 100%;
    margin: 0 auto;
  }
`;

export default function AddressTab() {
  const t = useTranslations("page.account");
  const describeMessage = useMessage();
  const shipping = useShippingForm(true);
  const [alert, setAlert] = useState<{ text: string; type: AlertType } | null>(null);

  async function save() {
    if (!shipping.validate()) {
      return;
    }
    try {
      await account.saveAddress(shipping.value);
      setAlert({ text: t("saved"), type: "success" });
    } catch (error) {
      if (isValidationError(error)) {
        shipping.setErrors(error.fieldErrors);
      } else if (error instanceof ApiError) {
        setAlert({ text: describeMessage(error.userMessage), type: "danger" });
      } else {
        throw error;
      }
    }
  }

  if (shipping.status === "loading") {
    return <AccountSpinner />;
  }
  if (shipping.status === "failed") {
    return <LoadFailed />;
  }
  return (
    <AddressForm>
      {alert && <Alert message={alert.text} type={alert.type} duration={3000} onClose={() => setAlert(null)} />}
      <ShippingDetailsForm value={shipping.value} errors={shipping.errors} onChange={shipping.onChange} />
      <div>
        <Button $usage="primary" $size="m" onClick={() => void save()}>
          {t("save")}
        </Button>
      </div>
    </AddressForm>
  );
}
