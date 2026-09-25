import Image from "next/image";
import Link from "next/link";
import styled from "styled-components";
import type { News } from "@festival/shared/api/types";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";
import HoverText from "../atoms/HoverText";
import { useFormatter, useTranslations } from "use-intl";

const ImageBox = styled.div`
  background-color: var(--main-white-smoke-color);
  padding: 30px 10px;
  height: 350px;
  width: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;

  @media screen and (min-width: 480px) {
    width: 250px;
  }

  @media screen and (min-width: 985px) {
    width: 350px;
  }
`;

const Wrapper = styled(Link)`
  background-color: var(--main-white-smoke-color);
  box-shadow: var(--default-box-shadow);
  display: flex;
  gap: 10px;
  padding-right: 10px;
  text-decoration: none;
  color: inherit;
  position: relative;

  &:hover ${HoverText} {
    display: block;
  }
`;

const InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
`;

const NewsDate = styled.div`
  margin-top: 20px;
  font-weight: bold;
  font-size: 0.9em;
`;

const Name = styled.div`
  margin: 25px 0;
  font-weight: bold;
  font-size: 1.2em;
`;

export default function NewsBox({ news }: Readonly<{ news: News }>) {
  const t = useTranslations("page.newsBox");
  const format = useFormatter();
  const localized = useLocalized();
  return (
    <Wrapper href={`/news/${news.id}`}>
      <ImageBox>
        <Image
          src={news.images[0] ?? "/images/no-image-found.webp"}
          alt=""
          fill
          sizes="350px"
          style={{ objectFit: "cover" }}
        />
      </ImageBox>
      <InfoWrapper>
        <NewsDate>
          {format.dateTime(new Date(news.createdAt), {
            weekday: "long",
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
          })}
        </NewsDate>
        <Name>{localized(news.name)}</Name>
      </InfoWrapper>
      <HoverText>{t("read")} &#8594;</HoverText>
    </Wrapper>
  );
}
