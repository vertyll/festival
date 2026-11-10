import "@/styles/globals.css";
import type { AppProps } from "next/app";
import Head from "next/head";
import { useTranslations } from "use-intl";
import type { FlatMessages } from "@festival/shared/api/types";
import { SessionProvider } from "@festival/shared/auth/session";
import { FestivalIntlProvider } from "@festival/shared/i18n/IntlSetup";
import { toLanguage } from "@festival/shared/i18n/localized";
import { appInitialProps } from "@festival/shared/react/appProps";
import AdminGate from "@/components/templates/AdminGate";
import { DialogProvider } from "@/lib/dialogs";
import { roboto } from "@/lib/fonts";

const PUBLIC_PAGES = new Set(["/terms", "/privacy"]);

interface AdminAppProps extends AppProps {
  messages: FlatMessages;
}

function AppHead() {
  const t = useTranslations("common.meta");
  return (
    <Head>
      <meta name="author" content={t("author")} />
      <meta name="description" content={t("adminDescription")} />
      <meta name="robots" content="noindex, nofollow" />
    </Head>
  );
}

export default function App({ Component, pageProps, router, messages }: Readonly<AdminAppProps>) {
  return (
    <FestivalIntlProvider language={toLanguage(router.locale)} messages={messages}>
      <AppHead />
      <SessionProvider client="admin">
        <div className={roboto.className}>
          {PUBLIC_PAGES.has(router.pathname) ? (
            <Component {...pageProps} />
          ) : (
            <AdminGate>
              <DialogProvider>
                <Component {...pageProps} />
              </DialogProvider>
            </AdminGate>
          )}
        </div>
      </SessionProvider>
    </FestivalIntlProvider>
  );
}

App.getInitialProps = appInitialProps;
