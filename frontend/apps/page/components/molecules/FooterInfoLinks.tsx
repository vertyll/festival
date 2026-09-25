import Link from "next/link";
import styled from "styled-components";
import { useTranslations } from "use-intl";

const StyledLink = styled(Link)`
  color: var(--light-text-color);
  text-decoration: none;
  margin: 0 15px;

  &:hover {
    text-decoration: underline;
  }
`;

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  flex-wrap: wrap;
`;

const Heading = styled.p`
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
    <Wrapper>
      <Heading>{t("heading")}</Heading>
      {LINKS.map((link) => (
        <StyledLink key={link.href} href={link.href}>
          {t(link.label)} &#10138;
        </StyledLink>
      ))}
    </Wrapper>
  );
}
