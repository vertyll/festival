import { useState } from "react";
import styled from "styled-components";
import { useTranslations } from "use-intl";

const NO_IMAGE = "/images/no-image-found.webp";

const Thumbnail = styled.img`
  max-width: 100%;
  max-height: 100%;
`;

const BigImage = styled.img<{ $maxHeight: number }>`
  max-width: 100%;
  max-height: ${(props) => props.$maxHeight}px;
`;

const ImageButtons = styled.div`
  display: flex;
  flex-grow: 0;
  gap: 10px;
  margin: 10px 0;
`;

const ImageButton = styled.button<{ $active: boolean }>`
  border: 2px solid ${(props) => (props.$active ? "var(--border-color-for-image)" : "transparent")};
  background: none;
  height: 70px;
  padding: 2px;
  cursor: pointer;
  border-radius: 5px;
`;

const BigImageWrapper = styled.div`
  text-align: center;
`;

const NoImageText = styled.div`
  font-size: 1rem;
  color: var(--gray-color);
  text-align: center;
`;

interface ImageGalleryProps {
  images: readonly string[];
  alt: string;
  maxHeight: number;
}

export default function ImageGallery({ images, alt, maxHeight }: Readonly<ImageGalleryProps>) {
  const t = useTranslations("common");
  const [active, setActive] = useState(images[0]);

  if (active === undefined) {
    return (
      <BigImageWrapper>
        <BigImage src={NO_IMAGE} alt={t("noImageAlt")} $maxHeight={maxHeight} />
        <NoImageText>{t("noImage")}</NoImageText>
      </BigImageWrapper>
    );
  }

  return (
    <>
      <BigImageWrapper>
        <BigImage src={active} alt={alt} $maxHeight={maxHeight} />
      </BigImageWrapper>
      {images.length > 1 && (
        <ImageButtons>
          {images.map((image) => (
            <ImageButton key={image} type="button" $active={image === active} onClick={() => setActive(image)}>
              <Thumbnail src={image} alt={alt} />
            </ImageButton>
          ))}
        </ImageButtons>
      )}
    </>
  );
}
