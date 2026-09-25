import { useEffect, useState } from "react";
import styled from "styled-components";
import { IconTopArrow } from "./icons";
import { useTranslations } from "use-intl";

const StyledButton = styled.button<{ $show: boolean }>`
  position: fixed;
  right: 20px;
  bottom: 20px;
  cursor: pointer;
  border: 0;
  color: var(--main-maize-color);
  width: 40px;
  height: 40px;
  padding: 5px;
  align-items: center;
  justify-content: center;
  background-color: var(--main-medium-slate-blue-color);
  border-radius: 5px;
  transition: 0.5s;
  display: ${({ $show }) => ($show ? "flex" : "none")};

  &:hover {
    color: var(--nav-color);
    filter: brightness(0.85);
  }
`;

const SCROLL_THRESHOLD = 100;

export default function ScrollToTop() {
  const t = useTranslations("common");
  const [show, setShow] = useState(false);

  useEffect(() => {
    const onScroll = () => setShow(window.scrollY > SCROLL_THRESHOLD);
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <StyledButton
      type="button"
      aria-label={t("scrollToTop")}
      $show={show}
      onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
    >
      <IconTopArrow />
    </StyledButton>
  );
}
