import Image from "next/image";
import { useState } from "react";
import styled from "styled-components";
import { useLocale, useTranslations } from "use-intl";
import type { SessionUser } from "@festival/shared/api/types";
import { signOut } from "@festival/shared/auth/session";
import Button, { ButtonLink } from "@/components/atoms/Button";
import RevealWrapper from "@/components/atoms/RevealWrapper";
import Tabs from "@/components/organisms/Tabs";
import AddressTab from "./AddressTab";
import OrdersTab from "./OrdersTab";
import WishlistTab from "./WishlistTab";

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

const Actions = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
`;

const TABS = ["orders", "wishlist", "address"] as const;
type Tab = (typeof TABS)[number];

export default function AccountPanels({
  user,
  accountUrl,
}: Readonly<{ user: SessionUser; accountUrl: string | null }>) {
  const t = useTranslations("page.account");
  const locale = useLocale();
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
        <Actions>
          {accountUrl && (
            <ButtonLink $usage="primary" $size="m" href={`${accountUrl}?kc_locale=${locale}`}>
              {t("settings")}
            </ButtonLink>
          )}
          <Button $usage="primary" $size="m" onClick={() => void signOut()}>
            {t("logout")}
          </Button>
        </Actions>
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
