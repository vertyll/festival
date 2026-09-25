import { createHttpClient, type HttpClient } from "./http";

export function serverHttp(): HttpClient {
  const backendUrl = process.env.BACKEND_INTERNAL_URL;
  if (!backendUrl) {
    throw new Error("Brak zmiennej środowiskowej BACKEND_INTERNAL_URL");
  }
  return createHttpClient(backendUrl);
}
