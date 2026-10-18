import stylistic from "@stylistic/eslint-plugin";
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
  {
    // The keycloak module is reached through its index, from the applications by the package's exports map and from
    // the rest of the shared package by this rule.
    files: ["packages/shared/src/**"],
    ignores: ["packages/shared/src/keycloak/**"],
    rules: {
      "no-restricted-imports": [
        "error",
        {
          patterns: [{ regex: "(^|/)keycloak/.+", message: "Use the keycloak module through what its index exports." }],
        },
      ],
    },
  },
  prettier,
  {
    // Comments only, as Prettier keeps code to its width but never wraps a comment; last, as the Prettier config
    // turns line length off.
    plugins: { "@stylistic": stylistic },
    rules: {
      "@stylistic/max-len": ["error", { code: Number.MAX_SAFE_INTEGER, comments: 120, ignoreUrls: true }],
    },
  },
];

export default config;
