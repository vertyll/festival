import { useRouter } from "next/router";
import styled from "styled-components";
import { useTranslations } from "use-intl";
import { signIn } from "@festival/shared/auth/session";
import Button from "@/components/atoms/Button";
import ErrorDiv from "@/components/atoms/ErrorDiv";
import LottieAnimation from "@/components/atoms/LottieAnimation";

const LoginWrapper = styled.div`
  display: flex;
`;

const LoginInfo = styled.div`
  max-width: 450px;
  width: 100%;
`;

export default function LoginPanel() {
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
