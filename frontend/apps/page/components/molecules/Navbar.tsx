import Link from "next/link";
import { useState, type ReactNode } from "react";
import styled, { css } from "styled-components";
import { useCartItems } from "@/lib/cart";
import { IconCart, IconHamburger, IconSearch, IconUser } from "../atoms/icons";
import { useTranslations } from "use-intl";
import LanguageSwitcher from "./LanguageSwitcher";

const StyledDiv = styled.div`
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
`;

const StyledLink = styled(Link)<{ $bold?: boolean }>`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  color: var(--dark-text-color);
  text-decoration: none;
  cursor: pointer;
  svg {
    height: 24px;
  }

  ${(props) =>
    props.$bold &&
    css`
      font-weight: bold;
      font-size: 0.9em;

      &:hover {
        filter: brightness(0.85);
      }
    `};

  @media (max-width: 768px) {
    display: block;
    padding: 10px 20px;
  }
`;

const StyledNav = styled.nav<{ $open?: boolean }>`
  display: flex;
  gap: 20px;
  align-items: center;

  @media (max-width: 985px) {
    display: ${(props) => (props.$open ? "flex" : "none")};
    flex-direction: column;
    width: 100%;
    margin-top: 25px;
    gap: 10px;
  }
`;

const NavButton = styled.button`
  background-color: transparent;
  border: none;
  width: 45px;
  height: 45px;
  color: var(--dark-text-color);
  cursor: pointer;

  @media (min-width: 985px) {
    display: none;
  }
`;

const MobileMenuWrapper = styled.div<{ $open: boolean }>`
  display: none;

  @media (max-width: 985px) {
    display: block;
    position: fixed;
    top: 0;
    right: 0;
    height: 100vh;
    width: 300px;
    background: var(--light-color);
    transform: translateX(${(props) => (props.$open ? "0" : "100%")});
    transition: transform 0.3s ease-in-out;
    z-index: 2;
  }
`;

const CloseButton = styled(NavButton)`
  position: absolute;
  top: 20px;
  right: 20px;
  display: block;
`;

const CenterDiv = styled.div`
  display: flex;
  align-items: center;
`;

const PageOverlay = styled.div<{ $open: boolean }>`
  display: none;

  @media (max-width: 985px) {
    display: ${(props) => (props.$open ? "block" : "none")};
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 1;
  }
`;

const SECTIONS = [
  { href: "/lineup", label: "lineup" },
  { href: "/news", label: "news" },
  { href: "/products", label: "shop" },
  { href: "/sponsors", label: "sponsors" },
  { href: "/info", label: "info" },
] as const;

function NavLinks({ cartCount, open }: Readonly<{ cartCount: number; open?: boolean }>): ReactNode {
  const t = useTranslations("page.nav");
  return (
    <StyledNav $open={open}>
      {SECTIONS.map((section) => (
        <StyledLink key={section.href} href={section.href} $bold>
          {t(section.label)}
        </StyledLink>
      ))}
      <StyledLink href="/search" $bold aria-label={t("search")}>
        <IconSearch />
      </StyledLink>
      <StyledLink href="/account">
        <IconUser />
        {t("account")}
      </StyledLink>
      <StyledLink href="/cart">
        <IconCart />
        {t("cart", { count: cartCount })}
      </StyledLink>
      <LanguageSwitcher />
    </StyledNav>
  );
}

export default function Navbar() {
  const t = useTranslations("page.nav");
  const cartCount = useCartItems().reduce((total, item) => total + item.quantity, 0);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const toggleMobileMenu = () => setIsMobileMenuOpen((open) => !open);

  return (
    <StyledDiv>
      <CenterDiv>
        <NavLinks cartCount={cartCount} />
        <NavButton type="button" aria-label={t("menu")} onClick={toggleMobileMenu}>
          <IconHamburger />
        </NavButton>
      </CenterDiv>
      <PageOverlay $open={isMobileMenuOpen} onClick={toggleMobileMenu} />
      <MobileMenuWrapper $open={isMobileMenuOpen}>
        {isMobileMenuOpen && (
          <CloseButton type="button" aria-label={t("closeMenu")} onClick={toggleMobileMenu}>
            <IconHamburger />
          </CloseButton>
        )}
        <NavLinks cartCount={cartCount} open={isMobileMenuOpen} />
      </MobileMenuWrapper>
    </StyledDiv>
  );
}
