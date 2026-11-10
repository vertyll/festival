import "@/styles/globals.css";
import { AnimatePresence } from "framer-motion";
import type { AppProps } from "next/app";
import Head from "next/head";
import { createGlobalStyle } from "styled-components";
import { useTranslations } from "use-intl";
import type { FlatMessages, ShopSettings } from "@festival/shared/api/types";
import { SessionProvider } from "@festival/shared/auth/session";
import { FestivalIntlProvider } from "@festival/shared/i18n/IntlSetup";
import { toLanguage } from "@festival/shared/i18n/localized";
import { appInitialProps } from "@festival/shared/react/appProps";
import ScrollToTop from "@/components/atoms/ScrollToTop";
import { roboto } from "@/lib/fonts";
import { ShopSettingsProvider } from "@/lib/shopSettings";
import { WishlistProvider } from "@/lib/wishlist";

const GlobalStyles = createGlobalStyle`
  body {
    font-family: ${roboto.style.fontFamily}, sans-serif;
    padding: 0;
    margin: 0;
    background-color: var(--main-background-color);
  }
`;

type PageProps = { shopSettings?: ShopSettings };

interface FestivalAppProps extends AppProps<PageProps> {
  messages: FlatMessages;
}

function AppHead() {
  const t = useTranslations("common.meta");
  return (
    <Head>
      <meta name="author" content={t("author")} />
      <meta name="description" content={t("pageDescription")} />
    </Head>
  );
}

export default function App({ Component, pageProps, router, messages }: Readonly<FestivalAppProps>) {
  return (
    <FestivalIntlProvider language={toLanguage(router.locale)} messages={messages}>
      <GlobalStyles />
      <AppHead />
      <SessionProvider client="page">
        <ShopSettingsProvider initial={pageProps.shopSettings ?? null}>
          <WishlistProvider>
            <AnimatePresence mode="wait">
              <Component {...pageProps} key={router.route} />
            </AnimatePresence>
            <ScrollToTop />
          </WishlistProvider>
        </ShopSettingsProvider>
      </SessionProvider>
    </FestivalIntlProvider>
  );
}

App.getInitialProps = appInitialProps;
