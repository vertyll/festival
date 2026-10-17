import type { HttpClient } from "../api/http";
import type { Language } from "../api/types";

/** Whether the signed-in person set up a second factor, and the credential Keycloak's `delete_credential` removes. */
export interface SecondFactor {
  enabled: boolean;
  credentialId: string | null;
}

/** The person's account on Keycloak, through the back-end, which calls Keycloak with the person's own token. */
export function keycloakAccountApi(http: HttpClient) {
  return {
    secondFactor: () => http.get<SecondFactor>("/api/account/second-factor"),
    keepLanguage: (language: Language) => http.put<{ language: Language }>("/api/account/language", { language }),
  };
}
