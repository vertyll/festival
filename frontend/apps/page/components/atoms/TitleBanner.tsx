import type { ReactNode } from "react";
import styled from "styled-components";

const Banner = styled.div`
  width: 100%;
  max-height: 325px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;

  img {
    width: 100%;
  }
`;

const Content = styled.div`
  z-index: 1;
`;

export default function TitleBanner({ imageUrl, children }: Readonly<{ imageUrl: string; children?: ReactNode }>) {
  return (
    <Banner>
      {/* eslint-disable-next-line @next/next/no-img-element -- statyczny baner z public/ */}
      <img src={imageUrl} alt="" />
      <Content>{children}</Content>
    </Banner>
  );
}
