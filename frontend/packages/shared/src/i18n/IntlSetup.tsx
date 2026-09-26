import { useRouter } from "next/router";
import { useEffect, useMemo, type ReactNode } from "react";
import { IntlProvider, useFormatter, useLocale, useTranslations } from "use-intl";
import { getCookie, setCookie } from "../browser/cookies";
import {
  LANGUAGES,
  type FlatMessages,
  type Language,
  type LocalizedText,
  type Message,
  type SelectedOption,
} from "../api/types";
import { resolveArgs, toLanguage } from "./localized";
import { nestMessages } from "./messages";

const TIME_ZONE = "Europe/Warsaw";
const LANGUAGE_COOKIE = "NEXT_LOCALE";
const LANGUAGE_COOKIE_DAYS = 365;

interface FestivalIntlProviderProps {
  language: Language;
  messages: FlatMessages;
  children: ReactNode;
}

export function FestivalIntlProvider({ language, messages, children }: Readonly<FestivalIntlProviderProps>) {
  const nested = useMemo(() => nestMessages(messages), [messages]);
  return (
    <IntlProvider
      locale={language}
      messages={nested}
      timeZone={TIME_ZONE}
      onError={(error) => console.error(error)}
      getMessageFallback={({ namespace, key }) => (namespace ? `${namespace}.${key}` : key)}
    >
      <PreferredLanguageRedirect />
      {children}
    </IntlProvider>
  );
}

function PreferredLanguageRedirect() {
  const router = useRouter();

  useEffect(() => {
    const preferred = LANGUAGES.find((language) => language === getCookie(LANGUAGE_COOKIE));
    if (preferred && preferred !== router.locale) {
      void router.replace(router.asPath, undefined, { locale: preferred });
    }
  }, [router]);

  return null;
}

export function useLanguage(): Language {
  return toLanguage(useLocale());
}

export function useLanguageSwitch(): {
  current: Language;
  languages: readonly Language[];
  switchTo: (language: Language) => void;
} {
  const router = useRouter();
  const current = useLanguage();
  return {
    current,
    languages: LANGUAGES,
    switchTo: (language) => {
      setCookie(LANGUAGE_COOKIE, language, LANGUAGE_COOKIE_DAYS);
      void router.push(router.asPath, undefined, { locale: language });
    },
  };
}

export function useLocalized(): (text: LocalizedText) => string {
  const language = useLanguage();
  return (text) => text[language];
}

export function useMessage(): (message: Message) => string {
  const t = useTranslations();
  const language = useLanguage();
  return (message) => t(message.code, resolveArgs(message.args, language));
}

export function useDescribeSelection(): (selection: readonly SelectedOption[]) => string {
  const t = useTranslations("common");
  const format = useFormatter();
  const language = useLanguage();
  return (selection) =>
    format.list(
      selection.map(({ option, value }) => t("selectedOption", { option: option[language], value: value[language] })),
      { type: "unit" }
    );
}
