import type { Message } from "@festival/shared/api/types";
import { useMessage } from "@festival/shared/i18n/IntlSetup";

export default function ErrorMessage({ message }: { message: Message | undefined }) {
  const describe = useMessage();
  return message ? <div className="error-message">{describe(message)}</div> : null;
}
