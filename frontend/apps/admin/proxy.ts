import type { NextRequest, NextResponse } from "next/server";
import { withContentSecurityPolicy } from "@festival/shared/security/proxy";

export function proxy(request: NextRequest): NextResponse {
  return withContentSecurityPolicy(request);
}

// Pages only: the back-end's own paths and static files carry no script to protect, and a prefetch is not rendered.
// The home page is listed on its own, as the locale Next.js adds to it leaves no slash for the second pattern. Next.js
// reads this object without running the module, so it is written out in full.
export const config = {
  matcher: [
    {
      source: "/",
      missing: [
        { type: "header", key: "next-router-prefetch" },
        { type: "header", key: "purpose", value: "prefetch" },
      ],
    },
    {
      source: "/((?!api|oauth2|login/oauth2|logout|_next/static|_next/image|favicon.ico).*)",
      missing: [
        { type: "header", key: "next-router-prefetch" },
        { type: "header", key: "purpose", value: "prefetch" },
      ],
    },
  ],
};
