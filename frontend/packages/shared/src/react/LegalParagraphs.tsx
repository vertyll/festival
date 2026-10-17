import { Fragment } from "react";
import { splitEmails } from "../format/emailLinks";

/**
 * The sections of each legal document: `legal.<document>.h<n>` and `p<n>` in the catalogue, the heading and the
 * paragraph of section `n`. A document of one section is a single paragraph, without a heading.
 */
export const LEGAL_SECTIONS = { privacyPolicy: 8, termsOfUse: 6, regulations: 1, bagPolicy: 1 } as const;

export type LegalDocumentName = keyof typeof LEGAL_SECTIONS;

export default function LegalParagraphs({
  document,
  heading,
  paragraph,
  headingClassName,
  linkClassName,
}: Readonly<{
  document: LegalDocumentName;
  heading: (section: number) => string;
  paragraph: (section: number) => string;
  headingClassName?: string;
  linkClassName?: string;
}>) {
  const count = LEGAL_SECTIONS[document];
  return Array.from({ length: count }, (_, index) => {
    const text = paragraph(index + 1);
    return (
      <section key={index}>
        {count > 1 && <h2 className={headingClassName}>{heading(index + 1)}</h2>}
        <p>
          {splitEmails(text).map((segment) =>
            segment.kind === "email" ? (
              <a key={segment.at} href={`mailto:${segment.email}`} className={linkClassName}>
                {segment.email}
              </a>
            ) : (
              <Fragment key={segment.at}>{segment.text}</Fragment>
            )
          )}
        </p>
      </section>
    );
  });
}
