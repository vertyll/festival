import type { ParsedUrlQuery } from "node:querystring";
import { ApiError } from "@festival/shared/api/http";
import { publicApi, type PublicApi } from "@festival/shared/api/public";
import { serverHttp } from "@festival/shared/api/serverHttp";

export function serverApi(): PublicApi {
  return publicApi(serverHttp());
}

export async function findOrNull<T>(load: () => Promise<T>): Promise<T | null> {
  try {
    return await load();
  } catch (error) {
    if (error instanceof ApiError && (error.status === 404 || error.status === 400)) {
      return null;
    }
    throw error;
  }
}

export function routeParam(params: ParsedUrlQuery | undefined, name: string): string {
  const value = params?.[name];
  if (typeof value !== "string") {
    throw new TypeError(`Brak parametru trasy "${name}"`);
  }
  return value;
}
