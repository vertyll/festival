import { fileURLToPath } from "node:url";

const BACKEND_PATHS = ["/api/:path*", "/oauth2/:path*", "/login/oauth2/:path*", "/logout"];

const LANGUAGES = ["pl", "en"];
const DEFAULT_LANGUAGE = "pl";

const workspaceRoot = fileURLToPath(new URL(".", import.meta.url));

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
    i18n: { locales: LANGUAGES, defaultLocale: DEFAULT_LANGUAGE },
    async rewrites() {
      const backendUrl = requiredEnv("BACKEND_INTERNAL_URL");
      return BACKEND_PATHS.map((path) => ({ source: path, destination: `${backendUrl}${path}` }));
    },
    ...appConfig,
  };
}
