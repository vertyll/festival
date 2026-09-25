import { createHttpClient, type HttpClient } from "./http";

export function serverHttp(): HttpClient {
  const backendUrl = process.env.BACKEND_INTERNAL_URL;
  if (!backendUrl) {
    throw new Error("Missing environment variable BACKEND_INTERNAL_URL");
  }
  return createHttpClient(backendUrl);
}
