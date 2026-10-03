import { splitEmails } from "../format/emailLinks";

export default function LegalParagraphs({
  content,
  linkClassName,
}: Readonly<{ content: string; linkClassName?: string }>) {
  return content.split("\n\n").map((paragraph) => (
    <p key={paragraph}>
      {splitEmails(paragraph).map((segment, index) =>
        segment.kind === "email" ? (
          <a key={index} href={`mailto:${segment.email}`} className={linkClassName}>
            {segment.email}
          </a>
        ) : (
          segment.text
        )
      )}
    </p>
  ));
}
