import { getCookie } from "../browser/cookies";
import type { Message, ProblemDetail } from "./types";

export type QueryValue = string | number | boolean | readonly string[] | undefined;
export type Query = Readonly<Record<string, QueryValue>>;

export interface HttpClient {
  get<T>(path: string, query?: Query): Promise<T>;
  getOptional<T>(path: string): Promise<T | null>;
  post<T>(path: string, body: unknown): Promise<T>;
  put<T>(path: string, body: unknown): Promise<T>;
  send(method: "POST" | "PUT" | "DELETE", path: string): Promise<void>;
  upload<T>(path: string, form: FormData): Promise<T>;
}

export class ApiError extends Error {
  readonly status: number;
  readonly problem: ProblemDetail | null;

  constructor(status: number, problem: ProblemDetail | null) {
    super(problem?.code ?? `errors.status.${status}`);
    this.name = "ApiError";
    this.status = status;
    this.problem = problem;
  }

  get userMessage(): Message {
    return this.problem ? { code: this.problem.code, args: this.problem.args } : { code: this.message, args: {} };
  }

  get fieldErrors(): Readonly<Record<string, Message>> {
    return this.problem?.errors ?? {};
  }
}

const CSRF_COOKIE = "XSRF-TOKEN";
const CSRF_HEADER = "X-XSRF-TOKEN";

export function createHttpClient(baseUrl: string): HttpClient {
  async function execute(method: string, path: string, body?: BodyInit, contentType?: string): Promise<Response> {
    const headers = new Headers({ Accept: "application/json" });
    if (contentType) {
      headers.set("Content-Type", contentType);
    }
    if (method !== "GET") {
      headers.set(CSRF_HEADER, await csrfToken());
    }
    const response = await fetch(baseUrl + path, { method, headers, body, credentials: "same-origin" });
    if (!response.ok) {
      throw new ApiError(response.status, await readProblem(response));
    }
    return response;
  }

  const json = (value: unknown) => JSON.stringify(value);

  return {
    async get<T>(path: string, query?: Query) {
      return (await execute("GET", path + toQueryString(query))).json() as Promise<T>;
    },
    async getOptional<T>(path: string) {
      const response = await execute("GET", path);
      return response.status === 204 ? null : ((await response.json()) as T);
    },
    async post<T>(path: string, body: unknown) {
      return (await execute("POST", path, json(body), "application/json")).json() as Promise<T>;
    },
    async put<T>(path: string, body: unknown) {
      return (await execute("PUT", path, json(body), "application/json")).json() as Promise<T>;
    },
    async send(method: "POST" | "PUT" | "DELETE", path: string) {
      await execute(method, path);
    },
    async upload<T>(path: string, form: FormData) {
      return (await execute("POST", path, form)).json() as Promise<T>;
    },
  };
}

export const browserHttp: HttpClient = createHttpClient("");

function toQueryString(query: Query | undefined): string {
  if (!query) {
    return "";
  }
  const params = new URLSearchParams();
  for (const [key, value] of Object.entries(query)) {
    if (value === undefined) {
      continue;
    }
    params.set(key, Array.isArray(value) ? value.join(",") : String(value));
  }
  const text = params.toString();
  return text ? `?${text}` : "";
}

async function readProblem(response: Response): Promise<ProblemDetail | null> {
  const type = response.headers.get("Content-Type") ?? "";
  return type.includes("json") ? ((await response.json()) as ProblemDetail) : null;
}

async function csrfToken(): Promise<string> {
  const existing = getCookie(CSRF_COOKIE);
  if (existing) {
    return existing;
  }
  await fetch("/api/me", { credentials: "same-origin" });
  const token = getCookie(CSRF_COOKIE);
  if (!token) {
    throw new Error("Back-end nie ustawił ciasteczka CSRF");
  }
  return token;
}
