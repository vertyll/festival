import { useEffect, useState } from "react";
import { getCookie, setCookie } from "../browser/cookies";

const CONSENT_COOKIE = "cookies_accepted";
const CONSENT_DAYS = 365;

export function useCookieConsent(): { consentMissing: boolean; accept: () => void } {
  const [consentMissing, setConsentMissing] = useState(false);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect -- ciasteczka istnieją dopiero w przeglądarce
    setConsentMissing(getCookie(CONSENT_COOKIE) === null);
  }, []);

  return {
    consentMissing,
    accept: () => {
      setCookie(CONSENT_COOKIE, "true", CONSENT_DAYS);
      setConsentMissing(false);
    },
  };
}
