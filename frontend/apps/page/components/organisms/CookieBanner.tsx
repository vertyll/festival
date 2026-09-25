import styled from "styled-components";
import { useCookieConsent } from "@festival/shared/react/useCookieConsent";
import Button from "../atoms/Button";
import { useTranslations } from "use-intl";

const BannerWrapper = styled.div`
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: var(--main-white-smoke-color);
  padding: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  z-index: 50;
`;

const Container = styled.div`
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;

  @media (min-width: 640px) {
    flex-direction: row;
    justify-content: space-between;
  }
`;

const Text = styled.p`
  font-size: 0.875rem;
  color: var(--dark-text-color);
`;

export default function CookieBanner() {
  const t = useTranslations("common.cookies");
  const { consentMissing, accept } = useCookieConsent();

  if (!consentMissing) {
    return null;
  }

  return (
    <BannerWrapper>
      <Container>
        <Text>{t("text")}</Text>
        <Button $usage="primary" $size="m" onClick={accept}>
          {t("accept")}
        </Button>
      </Container>
    </BannerWrapper>
  );
}
