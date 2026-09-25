import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import ts from "typescript";
import { createTranslator } from "use-intl";

const frontend = path.resolve(fileURLToPath(new URL("..", import.meta.url)));
const backend = path.resolve(frontend, "../backend");
const translationsDir = path.join(backend, "src/main/resources/i18n");
const sourceRoots = ["apps/page", "apps/admin", "packages/shared/src"].map((root) => path.join(frontend, root));

/**
 * @param {string} directory
 * @returns {string[]}
 */
function sourceFiles(directory) {
  return fs.readdirSync(directory, { withFileTypes: true }).flatMap((entry) => {
    const file = path.join(directory, entry.name);
    if (entry.isDirectory()) {
      return ["node_modules", ".next"].includes(entry.name) ? [] : sourceFiles(file);
    }
    return /\.tsx?$/.test(entry.name) && !entry.name.endsWith(".d.ts") ? [file] : [];
  });
}

function escape(text) {
  return text.replace(/[.*+?^${}()|[\]\\]/g, String.raw`\$&`);
}

function keyPattern(node) {
  if (ts.isStringLiteral(node) || ts.isNoSubstitutionTemplateLiteral(node)) {
    return escape(node.text);
  }
  if (ts.isTemplateExpression(node)) {
    return escape(node.head.text) + node.templateSpans.map((span) => `[^.]+${escape(span.literal.text)}`).join("");
  }
  return null;
}

function namespaceOf(identifier) {
  for (let scope = identifier.parent; scope; scope = scope.parent) {
    if (!ts.isBlock(scope) && !ts.isSourceFile(scope)) {
      continue;
    }
    for (const statement of scope.statements) {
      if (!ts.isVariableStatement(statement)) {
        continue;
      }
      for (const declaration of statement.declarationList.declarations) {
        const init = declaration.initializer;
        if (
          ts.isIdentifier(declaration.name) &&
          declaration.name.text === identifier.text &&
          init &&
          ts.isCallExpression(init) &&
          init.expression.getText() === "useTranslations"
        ) {
          const [namespace] = init.arguments;
          return namespace && ts.isStringLiteral(namespace) ? namespace.text : "";
        }
      }
    }
  }
  return undefined;
}

const exact = [];
const patterns = [];

function recordUsage(pattern, location) {
  (pattern.includes("[^.]+") || pattern.endsWith(".") ? patterns : exact).push({ pattern, location });
}

for (const file of sourceRoots.flatMap(sourceFiles)) {
  const source = ts.createSourceFile(file, fs.readFileSync(file, "utf8"), ts.ScriptTarget.Latest, true);
  const visit = (node) => {
    if (ts.isCallExpression(node) && node.arguments.length > 0) {
      const callee = node.expression;
      const target = ts.isPropertyAccessExpression(callee) && callee.name.text === "rich" ? callee.expression : callee;
      const location = `${path.relative(frontend, file)}:${source.getLineAndCharacterOfPosition(node.getStart()).line + 1}`;
      if (ts.isIdentifier(target) && target.text === "message") {
        const key = keyPattern(node.arguments[0]);
        if (key !== null) {
          recordUsage(key, location);
        }
      } else if (ts.isIdentifier(target)) {
        const namespace = namespaceOf(target);
        if (namespace !== undefined) {
          const prefix = namespace ? String.raw`${escape(namespace)}\.` : "";
          const key = keyPattern(node.arguments[0]);
          if (key !== null) {
            recordUsage(prefix + key, location);
          } else if (namespace) {
            recordUsage(prefix, location);
          }
        }
      }
    }
    ts.forEachChild(node, visit);
  };
  visit(source);
}

const messageKeys = fs.readFileSync(
  path.join(backend, "src/main/java/com/vertyll/festival/common/MessageKeys.java"),
  "utf8"
);
for (const [, name, key] of messageKeys.matchAll(/String (\w+) =\s*"([^"]+)"/g)) {
  recordUsage(escape(key), `MessageKeys.${name}`);
}

const catalogs = Object.fromEntries(
  fs
    .readdirSync(translationsDir)
    .filter((file) => file.endsWith(".json"))
    .map((file) => [
      path.basename(file, ".json"),
      JSON.parse(fs.readFileSync(path.join(translationsDir, file), "utf8")),
    ])
);
const keys = new Set(Object.keys(catalogs.pl));
const problems = [];

for (const [locale, messages] of Object.entries(catalogs)) {
  for (const [key, message] of Object.entries(messages)) {
    const tags = [...message.matchAll(/<(\w+)>/g)].map(([, tag]) => tag);
    const values = Object.fromEntries([
      ...[...message.matchAll(/\{\s*(\w+)\s*[,}]/g)].map(([, name]) => [name, 1]),
      ...tags.map((tag) => [tag, (chunks) => chunks]),
    ]);
    const report = (error) => problems.push(`${locale}.json: ${key}: ${error.message}`);
    const translate = createTranslator({ locale, messages: { message }, onError: report });
    try {
      if (tags.length > 0) {
        translate.rich("message", values);
      } else {
        translate("message", values);
      }
    } catch (error) {
      report(error);
    }
  }
}

for (const { pattern, location } of exact) {
  if (!keys.has(pattern.replace(/\\(.)/g, "$1"))) {
    problems.push(`${location}: missing key ${pattern.replace(/\\(.)/g, "$1")}`);
  }
}
const matchers = [...exact, ...patterns].map(({ pattern, location }) => ({
  regex: new RegExp(`^${pattern}${pattern.endsWith(".") ? "" : "$"}`),
  location,
}));
for (const { regex, location } of matchers) {
  if (regex.source.includes("[^.]+") && ![...keys].some((key) => regex.test(key))) {
    problems.push(`${location}: no key matches ${regex.source}`);
  }
}
for (const key of keys) {
  if (!matchers.some(({ regex }) => regex.test(key))) {
    problems.push(`unused key ${key}`);
  }
}

if (problems.length > 0) {
  console.error(problems.join("\n"));
  process.exit(1);
}
console.log(`Translations complete: ${keys.size} keys`);
