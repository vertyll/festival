import { Fragment } from "react";
import { splitEmails } from "../format/emailLinks";

export default function LegalParagraphs({
  content,
  linkClassName,
}: Readonly<{ content: string; linkClassName?: string }>) {
  return content.split("\n\n").map((paragraph) => (
    <p key={paragraph}>
      {splitEmails(paragraph).map((segment) =>
        segment.kind === "email" ? (
          <a key={segment.at} href={`mailto:${segment.email}`} className={linkClassName}>
            {segment.email}
          </a>
        ) : (
          <Fragment key={segment.at}>{segment.text}</Fragment>
        )
      )}
    </p>
  ));
}
