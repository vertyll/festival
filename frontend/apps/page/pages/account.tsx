import Image from "next/image";
import { useRouter } from "next/router";
import { useState } from "react";
import styled from "styled-components";
import { useTranslations } from "use-intl";
import { ApiError } from "@festival/shared/api/http";
import type { SessionUser } from "@festival/shared/api/types";
import { signIn, signOut, useSession } from "@festival/shared/auth/session";
import { useMessage } from "@festival/shared/i18n/IntlSetup";
import { isValidationError } from "@festival/shared/validation/validation";
import { LoadState } from "@festival/shared/react/LoadState";
import { useLoader } from "@festival/shared/react/useLoader";
import { Alert, type AlertType } from "@/components/atoms/Alert";
import Button from "@/components/atoms/Button";
import DivCenter from "@/components/atoms/DivCenter";
import ErrorDiv from "@/components/atoms/ErrorDiv";
import LoadFailed from "@/components/atoms/LoadFailed";
import LottieAnimation from "@/components/atoms/LottieAnimation";
import PageTitle from "@/components/atoms/PageTitle";
import RevealWrapper from "@/components/atoms/RevealWrapper";
import Spinner from "@/components/atoms/Spinner";
import ShippingDetailsForm from "@/components/molecules/ShippingDetailsForm";
import OrderSummary from "@/components/organisms/OrderSummary";
import ProductBox from "@/components/organisms/ProductBox";
import RevealGrid from "@/components/organisms/RevealGrid";
import Tabs from "@/components/organisms/Tabs";
import Layout from "@/components/templates/Layout";
import { account } from "@/lib/api";
import { useShippingForm } from "@/lib/shipping";
import { useWishlist } from "@/lib/wishlist";

const Wrapper = styled.div`
  display: flex;
  justify-content: center;
  gap: 50px;
  width: 75%;
  margin-top: 50px;

  @media screen and (max-width: 1400px) {
    width: 85%;
  }

  @media screen and (max-width: 950px) {
    width: 100%;
  }

  @media screen and (max-width: 768px) {
    flex-direction: column;
    align-items: center;
  }
`;

const Panel = styled.div`
  padding: 20px;
  border-radius: 20px;
  background-color: var(--main-white-smoke-color);
  box-shadow: var(--default-box-shadow);
  min-width: 0;

  @media screen and (max-width: 768px) {
    flex: 0 0 100%;
    width: 90%;
  }
`;

const ProfilePanel = styled(Panel)`
  flex: 0 0 30%;
  height: fit-content;
`;

const ContentPanel = styled(Panel)`
  flex: 1;
`;

const LoginWrapper = styled.div`
  display: flex;
`;

const LoginInfo = styled.div`
  max-width: 450px;
  width: 100%;
`;

const UserProfile = styled.div`
  display: flex;
  align-items: center;
  gap: 15px;
  background-color: var(--light-color);
  padding: 10px;
  border-radius: 20px;
  margin-bottom: 15px;

  img {
    border-radius: 50%;
  }
`;

const TabContent = styled.div`
  text-align: left;
  background-color: var(--light-color);
  padding: 10px;
  border-radius: 20px;

  @media screen and (min-width: 768px) {
    padding: 10px 20px;
    margin: 30px 0;
  }
`;

const AddressForm = styled(TabContent)`
  display: grid;

  & > div {
    max-width: 450px;
    width: 100%;
    margin: 0 auto;
  }
`;

const TABS = ["orders", "wishlist", "address"] as const;
type Tab = (typeof TABS)[number];

const SMALL_SPINNER = <Spinner size="2.5em" borderWidth="0.4em" />;

function LoginPanel() {
  const t = useTranslations("page.account");
  const { query } = useRouter();
  return (
    <LoginWrapper>
      <LoginInfo>
        <h2>{t("loginHeading")}</h2>
        {query.loginError !== undefined && <ErrorDiv>{t("loginError")}</ErrorDiv>}
        <p>
          <b>{t("loginBenefitsHeading")}</b>
        </p>
        <p>{t("loginBenefits")}</p>
        <Button $usage="primary" $size="m" onClick={() => signIn("page")}>
          {t("googleLogin")}
        </Button>
      </LoginInfo>
      <LottieAnimation name="login" style={{ maxWidth: "350px", height: "350px" }} />
    </LoginWrapper>
  );
}

function OrdersTab() {
  const t = useTranslations("page.account");
  const orders = useLoader(account.orders);
  return (
    <LoadState state={orders.state} loading={SMALL_SPINNER} failed={<LoadFailed />}>
      {(data) => (
        <TabContent>
          {data.length === 0 ? (
            <p>{t("noOrders")}</p>
          ) : (
            data.map((order) => <OrderSummary key={order.id} order={order} />)
          )}
        </TabContent>
      )}
    </LoadState>
  );
}

function WishlistTab() {
  const t = useTranslations("page.account");
  const products = useLoader(account.wishlist);
  const { productIds } = useWishlist();
  return (
    <LoadState state={products.state} loading={SMALL_SPINNER} failed={<LoadFailed />}>
      {(data) => {
        const wished = data.filter((product) => productIds.has(product.id));
        return (
          <TabContent>
            {wished.length === 0 ? (
              <p>{t("noWishlist")}</p>
            ) : (
              <RevealGrid items={wished} itemKey={(product) => product.id} columns={{ 0: 2, 1200: 3 }} gap={20}>
                {(product) => <ProductBox product={product} />}
              </RevealGrid>
            )}
          </TabContent>
        );
      }}
    </LoadState>
  );
}

function AddressTab() {
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
    return SMALL_SPINNER;
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

function AccountPanels({ user }: Readonly<{ user: SessionUser }>) {
  const t = useTranslations("page.account");
  const [activeTab, setActiveTab] = useState<Tab>("orders");
  const tabs = Object.fromEntries(TABS.map((tab) => [tab, t(`tabs.${tab}`)])) as Record<Tab, string>;
  return (
    <>
      <ProfilePanel>
        <h3>{t("profile")}</h3>
        <UserProfile>
          {user.picture && <Image src={user.picture} alt="" width={50} height={50} />}
          <div>{user.name ?? user.email}</div>
        </UserProfile>
        <Button $usage="primary" $size="m" onClick={() => void signOut("/")}>
          {t("logout")}
        </Button>
      </ProfilePanel>
      <ContentPanel>
        <RevealWrapper>
          <Tabs tabs={tabs} active={activeTab} onChange={setActiveTab} />
          {activeTab === "orders" && <OrdersTab />}
          {activeTab === "wishlist" && <WishlistTab />}
          {activeTab === "address" && <AddressTab />}
        </RevealWrapper>
      </ContentPanel>
    </>
  );
}

export default function AccountPage() {
  const t = useTranslations("page.account");
  const { session } = useSession();
  return (
    <>
      <PageTitle title={t("title")} />
      <Layout>
        <DivCenter>
          <Wrapper>
            {session.status === "loading" && <Spinner />}
            {session.status === "error" && <p>{t("sessionFailed")}</p>}
            {session.status === "anonymous" && <LoginPanel />}
            {session.status === "authenticated" && <AccountPanels user={session.user} />}
          </Wrapper>
        </DivCenter>
      </Layout>
    </>
  );
}
