import Image from "next/image";
import styled from "styled-components";
import type { Sponsor } from "@festival/shared/api/types";
import HoverText from "../atoms/HoverText";
import { useTranslations } from "use-intl";

const Box = styled.a`
  background-color: var(--main-white-smoke-color);
  padding: 30px 10px;
  height: 100px;
  width: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  box-shadow: var(--default-box-shadow);

  &:hover ${HoverText} {
    display: block;
  }
`;

export default function SponsorBox({ sponsor }: Readonly<{ sponsor: Sponsor }>) {
  const t = useTranslations("page.sponsorBox");
  return (
    <Box href={sponsor.link} target="_blank" rel="noopener noreferrer">
      <Image
        src={sponsor.images[0] ?? "/images/no-image-found.webp"}
        alt={sponsor.name}
        fill
        sizes="200px"
        style={{ objectFit: "cover" }}
      />
      <HoverText>{t("visit")} &#8594;</HoverText>
    </Box>
  );
}
