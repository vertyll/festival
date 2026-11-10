import type { NextPageContext } from "next";
import NextError, { type ErrorProps } from "next/error";
import Head from "next/head";
import styles from "./ErrorPage.module.css";

// Next.js's own wording, as its error page shows it; the catalog of translations comes from the back-end, which an
// error page cannot count on.
const STATUS_TITLES: Readonly<Record<number, string>> = {
  400: "Bad Request",
  404: "This page could not be found",
  405: "Method Not Allowed",
  500: "Internal Server Error",
};

const CLIENT_ERROR = "Application error: a client-side exception has occurred";

/**
 * The error page of an application, as Next.js draws its own but with its styles in classes, which the page's
 * Content-Security-Policy allows, rather than in `style` attributes.
 *
 * @param props - the status code, or none for an exception in the browser, and the host it happened on
 * @returns the page
 */
export function ErrorPage({ statusCode, hostname, title }: Readonly<ErrorProps>) {
  const heading = title || STATUS_TITLES[statusCode] || "An unexpected error has occurred";
  return (
    <div className={styles.error}>
      <Head>
        <title>{statusCode ? `${statusCode}: ${heading}` : CLIENT_ERROR}</title>
      </Head>
      <div className={styles.description}>
        {statusCode ? <h1 className={styles.status}>{statusCode}</h1> : null}
        <div className={styles.wrap}>
          <h2 className={styles.title}>
            {title || statusCode ? (
              heading
            ) : (
              <>
                {CLIENT_ERROR} {hostname ? `while loading ${hostname}` : null} (see the browser console for more
                information)
              </>
            )}
            .
          </h2>
        </div>
      </div>
    </div>
  );
}

ErrorPage.getInitialProps = (context: NextPageContext): Promise<ErrorProps> | ErrorProps =>
  NextError.getInitialProps(context);
