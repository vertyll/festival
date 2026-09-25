import Link from "next/link";
import { useState, type SubmitEvent } from "react";
import styled from "styled-components";
import { useTranslations } from "use-intl";
import type { Message } from "@festival/shared/api/types";
import { useMessage } from "@festival/shared/i18n/IntlSetup";
import { email as emailRule } from "@festival/shared/validation/validation";
import { Alert } from "../atoms/Alert";
import Button from "../atoms/Button";
import ErrorDiv from "../atoms/ErrorDiv";
import Input from "../atoms/Input";
import Title from "../atoms/Title";

const NewsletterContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background-color: var(--newsletter-color);
  margin: 200px 0;
`;

const Wrapper = styled.div`
  display: flex;
  gap: 10px;
`;

const PolicyLink = styled(Link)`
  text-decoration: underline;
  color: var(--dark-text-color);
  font-weight: bold;
  margin-bottom: 60px;

  &:hover {
    filter: brightness(0.85);
  }
`;

const validateEmail = emailRule();

export default function Newsletter() {
  const t = useTranslations("page.newsletter");
  const describe = useMessage();
  const [email, setEmail] = useState("");
  const [error, setError] = useState<Message | null>(null);
  const [alert, setAlert] = useState<string | null>(null);

  function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
    event.preventDefault();
    const validationError = validateEmail(email);
    setError(validationError);
    if (validationError === null) {
      setAlert(t("demo"));
      setEmail("");
    }
  }

  return (
    <NewsletterContainer>
      <Title style={{ marginTop: "10px" }}>{t("title")}</Title>
      <p>{t("subtitle")}</p>
      <PolicyLink href="/privacypolicy">{t("privacyPolicy")}</PolicyLink>
      <form onSubmit={handleSubmit} noValidate>
        <Wrapper>
          <Input
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            placeholder={t("placeholder")}
            aria-label={t("emailLabel")}
          />
          <div>
            <Button type="submit" $usage="primary" $size="m">
              {t("subscribe")}
            </Button>
          </div>
        </Wrapper>
        {error && <ErrorDiv>{describe(error)}</ErrorDiv>}
      </form>
      {alert && <Alert message={alert} duration={3000} onClose={() => setAlert(null)} />}
    </NewsletterContainer>
  );
}
