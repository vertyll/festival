import styled from "styled-components";
import { useTranslations } from "use-intl";
import { useLanguageSwitch } from "@festival/shared/i18n/IntlSetup";

const Switcher = styled.div`
  display: flex;
  gap: 5px;
`;

const LanguageButton = styled.button<{ $active: boolean }>`
  border: 0;
  background: none;
  cursor: pointer;
  font-weight: ${(props) => (props.$active ? "bold" : "normal")};
  text-decoration: ${(props) => (props.$active ? "underline" : "none")};
  color: var(--dark-text-color);
`;

export default function LanguageSwitcher() {
  const t = useTranslations("common.language");
  const { current, languages, switchTo } = useLanguageSwitch();
  return (
    <Switcher role="group" aria-label={t("label")}>
      {languages.map((language) => (
        <LanguageButton
          key={language}
          type="button"
          lang={language}
          $active={language === current}
          aria-pressed={language === current}
          aria-label={t(`name.${language}`)}
          onClick={() => switchTo(language)}
        >
          {t(`short.${language}`)}
        </LanguageButton>
      ))}
    </Switcher>
  );
}
