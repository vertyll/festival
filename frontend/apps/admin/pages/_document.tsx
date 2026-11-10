import Document, { Head, Html, Main, NextScript, type DocumentContext, type DocumentInitialProps } from "next/document";
import { nonceOf } from "@festival/shared/security/nonce";

interface AdminDocumentProps extends DocumentInitialProps {
  nonce: string | undefined;
}

export default class AdminDocument extends Document<AdminDocumentProps> {
  static override async getInitialProps(context: DocumentContext): Promise<AdminDocumentProps> {
    const initialProps = await Document.getInitialProps(context);
    return { ...initialProps, nonce: nonceOf(context.req?.headers) };
  }

  override render() {
    const { nonce } = this.props;
    return (
      <Html lang={this.props.locale}>
        <Head nonce={nonce} />
        <body>
          <Main />
          <NextScript nonce={nonce} />
        </body>
      </Html>
    );
  }
}
