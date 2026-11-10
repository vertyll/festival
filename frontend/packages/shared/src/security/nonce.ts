import type { IncomingHttpHeaders } from "node:http";
import { NONCE_HEADER } from "./contentSecurityPolicy";

/**
 * The nonce the proxy gave this page, for `_document` to put on the scripts it writes.
 *
 * @param headers - the headers of the request for the page, absent when a page is exported at build time
 * @returns the nonce, or `undefined` for a page the proxy did not see
 */
export function nonceOf(headers: IncomingHttpHeaders | undefined): string | undefined {
  const nonce = headers?.[NONCE_HEADER];
  return typeof nonce === "string" ? nonce : undefined;
}
