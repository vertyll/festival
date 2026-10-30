import { useRouter } from "next/router";
import { useEffect } from "react";
import styled from "styled-components";
import { useTranslations } from "use-intl";
import { signIn, thisPage } from "@festival/shared/auth/session";
import Button from "@/components/atoms/Button";
import ErrorDiv from "@/components/atoms/ErrorDiv";
import Spinner from "@/components/atoms/Spinner";

const LoginInfo = styled.div`
  max-width: 450px;
  width: 100%;
`;

/**
 * Sends a signed-out reader straight to the sign-in, and back here after, showing nothing but the spinner on the way.
 * When a sign-in has just failed, it says so, with a button to try again, rather than going round in a loop.
 */
export default function LoginPanel() {
  const t = useTranslations("page.account");
  const { isReady, query } = useRouter();
  const failed = query.loginError !== undefined;

  useEffect(() => {
    if (isReady && !failed) {
      signIn("page", thisPage());
    }
  }, [isReady, failed]);

  if (!failed) {
    return <Spinner />;
  }
  return (
    <LoginInfo>
      <h2>{t("loginHeading")}</h2>
      <ErrorDiv>{t("loginError")}</ErrorDiv>
      <Button $usage="primary" $size="m" onClick={() => signIn("page", thisPage())}>
        {t("login")}
      </Button>
    </LoginInfo>
  );
}
