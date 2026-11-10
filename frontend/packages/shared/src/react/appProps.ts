import NextApp, { type AppContext, type AppInitialProps } from "next/app";
import type { FlatMessages } from "../api/types";
import { toLanguage } from "../i18n/localized";
import { loadMessages } from "../i18n/messages";
import { ErrorPage, loadErrorPageMessages } from "./ErrorPage";

/**
 * The initial props of an application's `_app`: those Next.js gives every page, with the catalog of translations in
 * the page's language. The error page takes its catalog from {@link loadErrorPageMessages}, which falls back to English
 * when the catalog cannot be loaded.
 *
 * @param context - the context Next.js passes to `_app`
 * @returns the page's props and its messages
 */
export async function appInitialProps(context: AppContext): Promise<AppInitialProps & { messages: FlatMessages }> {
  const language = toLanguage(context.ctx.locale ?? context.router.locale);
  const loadPageMessages = context.Component === ErrorPage ? loadErrorPageMessages : loadMessages;
  const [appProps, messages] = await Promise.all([NextApp.getInitialProps(context), loadPageMessages(language)]);
  return { ...appProps, messages };
}
