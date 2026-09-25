import Document, { Head, Html, Main, NextScript } from "next/document";

export default class AdminDocument extends Document {
  override render() {
    return (
      <Html lang={this.props.locale}>
        <Head />
        <body>
          <Main />
          <NextScript />
        </body>
      </Html>
    );
  }
}
