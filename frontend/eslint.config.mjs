import nextCoreWebVitals from "eslint-config-next/core-web-vitals";
import nextTypescript from "eslint-config-next/typescript";
import prettier from "eslint-config-prettier/flat";

const config = [
  { ignores: ["**/.next/**", "**/node_modules/**", "**/next-env.d.ts"] },
  ...nextCoreWebVitals,
  ...nextTypescript,
  {
    settings: { next: { rootDir: ["apps/*/"] } },
    rules: {
      "@typescript-eslint/consistent-type-imports": "error",
      "@typescript-eslint/no-non-null-assertion": "error",
      eqeqeq: ["error", "always"],
    },
  },
  prettier,
];

export default config;
