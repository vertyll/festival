import Link from "next/link";
import styled from "styled-components";
import { IconCreditCart } from "../atoms/icons";
import { useTranslations } from "use-intl";

const StyledDiv = styled.div`
  background-size: cover;
  background-position: center;
  width: 100%;
  height: 525px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  gap: 50px;
  background-image: url("/images/baner.webp");
`;

const Title = styled.h1`
  margin: 0;
  font-weight: bold;
  font-size: 3em;
  font-family: "Almendra", serif;
  color: var(--light-color);
  text-shadow: 3px 3px 6px rgba(14, 14, 14, 1);
`;

const ShopLink = styled(Link)`
  display: inline-flex;
  align-items: center;
  border-radius: 30px;
  padding: 15px 35px;
  font-size: 1.2rem;
  background-color: var(--main-maize-color);
  color: var(--dark-text-color);
  text-decoration: none;
  transition: 0.5s;

  svg {
    height: 24px;
    margin-right: 5px;
  }

  &:hover {
    filter: brightness(0.85);
  }
`;

export default function Banner() {
  const t = useTranslations("page.banner");
  return (
    <StyledDiv>
      <Title>{t("title")}</Title>
      <ShopLink href="/products">
        <IconCreditCart />
        {t("buyTicket")}
      </ShopLink>
    </StyledDiv>
  );
}
