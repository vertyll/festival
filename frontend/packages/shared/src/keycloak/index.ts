// The way into Keycloak's pages and the person's account there, for the rest of the front-ends. Only what this file
// exports is reachable from outside: the package's exports map `./keycloak/*` to nothing.
import { browserHttp } from "../api/http";
import { LANGUAGES, type Language } from "../api/types";
import type { LoginClient } from "../auth/session";
import { keycloakAccountApi, type SecondFactor } from "./accountApi";
import { readShared, writeShared } from "./sharedCookie";

export type { SecondFactor } from "./accountApi";

/**
 * The language the reader last chose, in the application or on Keycloak's pages, which both write: a shared record
 * rather than a message, so the two agree however the reader moves between them.
 */
const SHARED_LANGUAGE_COOKIE = "ui_locale_festival";

/**
 * A page Keycloak shows to a signed-in person before returning: changing the password, setting up the second factor, or
 * removing a credential named by its id, which Keycloak asks to confirm.
 */
export type AccountAction = "UPDATE_PASSWORD" | "CONFIGURE_TOTP" | `delete_credential:${string}`;

export function startAccountAction(client: LoginClient, action: AccountAction): void {
  // eslint-disable-next-line @next/next/no-location-assign-relative-destination
  window.location.assign(`/oauth2/authorization/${client}?kc_action=${action}`);
}

/** The language last chosen here or on Keycloak's pages, when one was. */
export function chosenLanguage(): Language | undefined {
  return LANGUAGES.find((language) => language === readShared(SHARED_LANGUAGE_COOKIE));
}

/**
 * Records a language chosen here, for Keycloak's pages: in the shared cookie, and on the signed-in person's account,
 * which Keycloak shows a signed-in person in.
 */
export function languageChosen(language: Language, signedIn: boolean): void {
  writeShared(SHARED_LANGUAGE_COOKIE, language);
  if (signedIn) {
    void keycloakAccountApi(browserHttp)
      .keepLanguage(language)
      .catch(() => undefined);
  }
}

/** The person's second factor, and the credential Keycloak removes to turn it off. */
export function secondFactor(): Promise<SecondFactor> {
  return keycloakAccountApi(browserHttp).secondFactor();
}
