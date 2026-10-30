import { useRouter } from "next/router";
import { useEffect } from "react";
import { signIn, thisPage } from "@festival/shared/auth/session";
import Spinner from "@/components/atoms/Spinner";

/** Sends a signed-out reader straight to the sign-in, and back here after, showing the spinner on the way. */
export default function LoginPanel() {
  const { isReady } = useRouter();

  useEffect(() => {
    if (isReady) {
      signIn("page", thisPage());
    }
  }, [isReady]);

  return <Spinner />;
}
