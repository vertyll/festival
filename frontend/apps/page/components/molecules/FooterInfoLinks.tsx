import Link from "next/link";
import styled from "styled-components";
import { useTranslations } from "use-intl";

const StyledLink = styled(Link)`
  color: var(--light-text-color);
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
`;

const Wrapper = styled.nav`
  display: flex;
  flex-direction: column;
  gap: 12px;
`;

const Links = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 8px 24px;
`;

const Heading = styled.p`
  margin: 0;
  color: var(--main-maize-color);
`;

const LINKS = [
  { href: "/privacypolicy", label: "privacy" },
  { href: "/termsofuse", label: "terms" },
  { href: "/regulations", label: "regulations" },
  { href: "/bagpolicy", label: "bagPolicy" },
] as const;

export default function FooterInfoLinks() {
  const t = useTranslations("page.footer");
  return (
    <Wrapper aria-label={t("heading")}>
      <Heading>{t("heading")}</Heading>
      <Links>
        {LINKS.map((link) => (
          <StyledLink key={link.href} href={link.href}>
            {t(link.label)} &#10138;
          </StyledLink>
        ))}
      </Links>
    </Wrapper>
  );
}
