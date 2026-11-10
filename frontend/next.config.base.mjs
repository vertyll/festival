import { fileURLToPath } from "node:url";

const BACKEND_PATHS = ["/api/:path*", "/oauth2/:path*", "/login/oauth2/:path*", "/logout"];

const LANGUAGES = ["pl", "en"];
const DEFAULT_LANGUAGE = "pl";

const workspaceRoot = fileURLToPath(new URL(".", import.meta.url));

// The Content-Security-Policy, which needs a nonce for each page, is sent by each application's proxy.ts.
const SECURITY_HEADERS = [
  { key: "Strict-Transport-Security", value: "max-age=31536000; includeSubDomains" },
  { key: "X-Content-Type-Options", value: "nosniff" },
  { key: "X-Frame-Options", value: "DENY" },
  { key: "Referrer-Policy", value: "strict-origin-when-cross-origin" },
];

function requiredEnv(name) {
  const value = process.env[name];
  if (!value) {
    throw new Error(`Missing environment variable ${name} (internal back-end URL)`);
  }
  return value;
}

export function createNextConfig(appConfig = {}) {
  return {
    output: "standalone",
    reactStrictMode: true,
    outputFileTracingRoot: workspaceRoot,
    transpilePackages: ["@festival/shared"],
    images: { unoptimized: true },
    i18n: { locales: LANGUAGES, defaultLocale: DEFAULT_LANGUAGE, localeDetection: false },
    async rewrites() {
      const backendUrl = requiredEnv("BACKEND_INTERNAL_URL");
      return BACKEND_PATHS.map((path) => ({ source: path, destination: `${backendUrl}${path}` }));
    },
    async headers() {
      return [{ source: "/:path*", headers: SECURITY_HEADERS }];
    },
    ...appConfig,
  };
}
