import type { NextPage } from "next";
import NextError, { type ErrorProps } from "next/error";
import Head from "next/head";
import { useTranslations } from "use-intl";
import type { FlatMessages, Language } from "../api/types";
import { loadMessages } from "../i18n/messages";
import styles from "./ErrorPage.module.css";

// The error page's own messages in English, for when the catalog cannot be loaded: it comes from the back-end, which
// may be the very thing that failed.
const FALLBACK_MESSAGES: FlatMessages = {
  "common.errorPage.status.400": "Bad Request",
  "common.errorPage.status.404": "This page could not be found",
  "common.errorPage.status.405": "Method Not Allowed",
  "common.errorPage.status.500": "Internal Server Error",
  "common.errorPage.unexpected": "An unexpected error has occurred",
  "common.errorPage.title": "{status}: {reason}",
  "common.errorPage.reason": "{reason}.",
  "common.errorPage.clientError": "Application error: a client-side exception has occurred",
  "common.errorPage.clientErrorDetails":
    "Application error: a client-side exception has occurred (see the browser console for more information).",
  "common.errorPage.clientErrorDetailsOnHost":
    "Application error: a client-side exception has occurred while loading {host} (see the browser console for more information).",
};

/**
 * The catalog of translations for the error page: the catalog itself, or the error page's own messages in English
 * when it cannot be loaded.
 *
 * @param language - the language of the page
 * @returns the messages
 */
export async function loadErrorPageMessages(language: Language): Promise<FlatMessages> {
  try {
    return await loadMessages(language);
  } catch (error) {
    console.error("The catalog of translations could not be loaded; the error page is shown in English", error);
    return FALLBACK_MESSAGES;
  }
}

/**
 * The error page of an application, as Next.js draws its own but with its styles in classes, which the page's
 * Content-Security-Policy allows, rather than in `style` attributes, and with its text from the catalog.
 *
 * @param props - the status code, or none for an exception in the browser, and the host it happened on
 * @returns the page
 */
export const ErrorPage: NextPage<ErrorProps> = ({ statusCode, hostname, title }) => {
  const t = useTranslations("common.errorPage");
  const statusKey = `status.${statusCode}`;
  const reason = title || (t.has(statusKey) ? t(`status.${statusCode}`) : t("unexpected"));
  const clientError = hostname ? t("clientErrorDetailsOnHost", { host: hostname }) : t("clientErrorDetails");
  return (
    <div className={styles.error}>
      <Head>
        <title>{statusCode ? t("title", { status: statusCode, reason }) : t("clientError")}</title>
      </Head>
      <div className={styles.description}>
        {statusCode ? <h1 className={styles.status}>{statusCode}</h1> : null}
        <div className={styles.wrap}>
          <h2 className={styles.title}>{title || statusCode ? t("reason", { reason }) : clientError}</h2>
        </div>
      </div>
    </div>
  );
};

ErrorPage.getInitialProps = NextError.getInitialProps;
