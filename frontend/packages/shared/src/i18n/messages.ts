import type { AbstractIntlMessages } from "use-intl";
import { browserHttp } from "../api/http";
import { publicApi } from "../api/public";
import { serverHttp } from "../api/serverHttp";
import type { FlatMessages, Language } from "../api/types";

export function nestMessages(flat: FlatMessages): AbstractIntlMessages {
  const root: Record<string, AbstractIntlMessages | string> = {};
  for (const [key, message] of Object.entries(flat)) {
    const path = key.split(".");
    const leaf = path.pop();
    if (leaf === undefined) {
      throw new Error("Empty translation key");
    }
    let node = root;
    for (const segment of path) {
      const next = node[segment] ?? {};
      if (typeof next === "string") {
        throw new TypeError(`Translation key ${key} collides with another key`);
      }
      node[segment] = next;
      node = next as Record<string, AbstractIntlMessages | string>;
    }
    node[leaf] = message;
  }
  return root;
}

const clientCache = new Map<Language, Promise<FlatMessages>>();

export function loadMessages(language: Language): Promise<FlatMessages> {
  if (typeof window === "undefined") {
    return publicApi(serverHttp()).translations(language);
  }
  const cached = clientCache.get(language);
  if (cached) {
    return cached;
  }
  const loading = publicApi(browserHttp).translations(language);
  clientCache.set(language, loading);
  loading.catch(() => clientCache.delete(language));
  return loading;
}
