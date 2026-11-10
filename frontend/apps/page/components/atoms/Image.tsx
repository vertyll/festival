import { getImageProps, type ImageProps } from "next/image";
import styled, { css } from "styled-components";

const Img = styled.img<{ $fill: boolean; $fit: "cover" | "contain" | undefined }>`
  color: transparent;
  ${(props) =>
    props.$fill &&
    css`
      position: absolute;
      inset: 0;
      width: 100%;
      height: 100%;
    `}
  ${(props) => props.$fit && `object-fit: ${props.$fit};`}
`;

type ImageAttributes = Omit<ImageProps, "style" | "objectFit"> & {
  /** How the picture fills its box, as `object-fit` does. */
  fit?: "cover" | "contain";
};

/**
 * A picture laid out as `next/image` lays it out, with its styles in a class rather than in a `style` attribute, which
 * the page's Content-Security-Policy does not allow.
 */
export default function Image({ fit, ...attributes }: Readonly<ImageAttributes>) {
  const { props } = getImageProps(attributes);
  return <Img {...props} style={undefined} $fill={attributes.fill === true} $fit={fit} />;
}
