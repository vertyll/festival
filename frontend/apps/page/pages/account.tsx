import styled from "styled-components";
import { useTranslations } from "use-intl";
import { useSession } from "@festival/shared/auth/session";
import DivCenter from "@/components/atoms/DivCenter";
import PageTitle from "@/components/atoms/PageTitle";
import Spinner from "@/components/atoms/Spinner";
import AccountPanels from "@/components/organisms/account/AccountPanels";
import LoginPanel from "@/components/organisms/account/LoginPanel";
import Layout from "@/components/templates/Layout";

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
