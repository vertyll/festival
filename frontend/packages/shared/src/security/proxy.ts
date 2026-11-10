import { NextResponse, type NextRequest } from "next/server";
import { NONCE_HEADER, contentSecurityPolicy } from "./contentSecurityPolicy";

// 128 bits from a cryptographically secure generator, as CSP Level 3 asks of a nonce.
const NONCE_BYTES = 16;

function createNonce(): string {
  return btoa(String.fromCodePoint(...crypto.getRandomValues(new Uint8Array(NONCE_BYTES))));
}

/**
 * Gives the page a nonce of its own: in the request, for `_document` to put on the scripts it writes, and in the
 * Content-Security-Policy of the response.
 *
 * @param request - the request for a page
 * @returns the response, with its Content-Security-Policy
 */
export function withContentSecurityPolicy(request: NextRequest): NextResponse {
  const nonce = createNonce();
  const policy = contentSecurityPolicy(nonce, process.env.NODE_ENV === "development");
  const headers = new Headers(request.headers);
  headers.set(NONCE_HEADER, nonce);
  headers.set("Content-Security-Policy", policy);
  const response = NextResponse.next({ request: { headers } });
  response.headers.set("Content-Security-Policy", policy);
  return response;
}
