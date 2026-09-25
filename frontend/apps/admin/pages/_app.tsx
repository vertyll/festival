import "@/styles/globals.css";
import NextApp, { type AppContext, type AppProps } from "next/app";
import Head from "next/head";
import { useTranslations } from "use-intl";
import type { FlatMessages } from "@festival/shared/api/types";
import { SessionProvider } from "@festival/shared/auth/session";
import { FestivalIntlProvider } from "@festival/shared/i18n/IntlSetup";
import { toLanguage } from "@festival/shared/i18n/localized";
import { loadMessages } from "@festival/shared/i18n/messages";
import { DialogProvider } from "@/lib/dialogs";

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
      <SessionProvider>
        <DialogProvider>
          <Component {...pageProps} />
        </DialogProvider>
      </SessionProvider>
    </FestivalIntlProvider>
  );
}

App.getInitialProps = async (context: AppContext) => {
  const [appProps, messages] = await Promise.all([
    NextApp.getInitialProps(context),
    loadMessages(toLanguage(context.ctx.locale ?? context.router.locale)),
  ]);
  return { ...appProps, messages };
};
