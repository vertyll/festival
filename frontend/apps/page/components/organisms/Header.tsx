import { useTranslations } from "use-intl";
import Link from "next/link";
import styled from "styled-components";
import Navbar from "../molecules/Navbar";

const StyledHeader = styled.header`
  margin: 0 10px;
  background-color: var(--nav-color);
  border-bottom: 1px solid var(--nav-border-bottom-color);
`;

const Wrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
`;

const LogoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
`;

const Logo = styled(Link)`
  color: var(--dark-text-color);
  text-decoration: none;
  padding: 0 20px;
  font-family: "Almendra", serif;
  font-size: 1.44em;
`;

const Place = styled.span`
  font-size: 0.9em;
`;

export default function Header() {
  const t = useTranslations("page.header");
  return (
    <StyledHeader>
      <Wrapper>
        <LogoWrapper>
          <Logo href="/">{t("logo")}</Logo>
          <span>{t.rich("dates", { b: (chunks) => <b>{chunks}</b> })}</span>
          <Place>{t("venue")}</Place>
        </LogoWrapper>
        <Navbar />
      </Wrapper>
    </StyledHeader>
  );
}
