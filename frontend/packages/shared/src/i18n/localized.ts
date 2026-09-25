import { LANGUAGES, type Language, type LocalizedText, type MessageArgs } from "../api/types";

export function toLanguage(locale: string | undefined): Language {
  const language = LANGUAGES.find((candidate) => candidate === locale);
  if (!language) {
    throw new Error(`Unsupported locale: ${locale}`);
  }
  return language;
}

export function emptyLocalizedText(): LocalizedText {
  return Object.fromEntries(LANGUAGES.map((language) => [language, ""])) as Record<Language, string>;
}

export function isBlankLocalizedText(text: LocalizedText): boolean {
  return LANGUAGES.every((language) => text[language].trim() === "");
}

export function withLanguage(text: LocalizedText, language: Language, value: string): LocalizedText {
  return { ...text, [language]: value };
}

export function resolveArgs(args: MessageArgs, language: Language): Record<string, string | number> {
  return Object.fromEntries(
    Object.entries(args).map(([name, value]) => [name, typeof value === "object" ? value[language] : value])
  );
}
