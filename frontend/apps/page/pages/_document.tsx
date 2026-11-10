import Document, { Head, Html, Main, NextScript, type DocumentContext, type DocumentInitialProps } from "next/document";
import { ServerStyleSheet } from "styled-components";
import { nonceOf } from "@festival/shared/security/nonce";

interface FestivalDocumentProps extends DocumentInitialProps {
  nonce: string | undefined;
}

export default class FestivalDocument extends Document<FestivalDocumentProps> {
  static override async getInitialProps(context: DocumentContext): Promise<FestivalDocumentProps> {
    const nonce = nonceOf(context.req?.headers);
    const sheet = new ServerStyleSheet({ nonce });
    const originalRenderPage = context.renderPage;
    try {
      context.renderPage = () =>
        originalRenderPage({
          enhanceApp: (App) => (props) => sheet.collectStyles(<App {...props} />),
        });
      const initialProps = await Document.getInitialProps(context);
      return {
        ...initialProps,
        styles: [initialProps.styles, sheet.getStyleElement()],
        nonce,
      };
    } finally {
      sheet.seal();
    }
  }

  override render() {
    const { nonce } = this.props;
    return (
      <Html lang={this.props.locale}>
        <Head nonce={nonce}>
          {/* Where styled-components finds the nonce for the styles it adds in the browser. */}
          <meta property="csp-nonce" nonce={nonce} />
        </Head>
        <body>
          <Main />
          <NextScript nonce={nonce} />
        </body>
      </Html>
    );
  }
}
