import Image from "next/image";
import Link from "next/link";
import styled from "styled-components";
import type { Artist } from "@festival/shared/api/types";
import { dateOnly } from "@festival/shared/format/format";
import HoverText from "../atoms/HoverText";
import { useFormatter, useTranslations } from "use-intl";

const Box = styled(Link)`
  background-color: var(--main-white-smoke-color);
  padding: 30px 10px;
  height: 100px;
  width: 170px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;

  &:hover ${HoverText} {
    display: block;
  }
`;

const Name = styled(Link)`
  font-weight: bold;
  font-size: 1.2em;
  text-decoration: none;
  color: inherit;
`;

const Wrapper = styled.div`
  background-color: var(--main-white-smoke-color);
  padding: 20px;
  box-shadow: var(--default-box-shadow);
`;

const ArtistName = styled.div`
  margin: 5px 0;
`;

const ConcertDate = styled(Link)`
  display: block;
  margin-top: 10px;
  font-weight: bold;
  font-size: 0.9em;
  text-decoration: none;
  color: inherit;
`;

export default function ArtistBox({ artist }: Readonly<{ artist: Artist }>) {
  const t = useTranslations("page.artistBox");
  const format = useFormatter();
  const url = `/artist/${artist.id}`;
  return (
    <Wrapper>
      <ArtistName>
        <Name href={url}>{artist.name}</Name>
      </ArtistName>
      <Box href={url}>
        <Image
          src={artist.images[0] ?? "/images/no-image-found.webp"}
          alt=""
          fill
          sizes="170px"
          style={{ objectFit: "cover" }}
        />
        <HoverText>{t("see")} &#8594;</HoverText>
      </Box>
      <ConcertDate href={url}>
        {artist.concertDate
          ? format.dateTime(dateOnly(artist.concertDate), {
              weekday: "long",
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
            })
          : t("dateSoon")}
      </ConcertDate>
    </Wrapper>
  );
}
