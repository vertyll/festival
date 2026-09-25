import type { ReactNode } from "react";
import styled from "styled-components";
import BackLink from "../atoms/BackLink";
import SingleBox from "../atoms/SingleBox";
import ImageGallery from "../organisms/ImageGallery";
import PageTitle from "../atoms/PageTitle";
import Layout from "./Layout";

const ColWrapper = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  width: 100%;
  gap: 20px;

  @media screen and (min-width: 768px) {
    grid-template-columns: 1fr 1fr;
  }
`;

const Column = styled.div`
  display: flex;
  gap: 20px;
  flex-direction: column;
  margin: 0 30px;
  white-space: pre-line;

  @media screen and (min-width: 768px) {
    margin: 0 30px 0 0;
  }
`;

const Title = styled.h1`
  font-size: 2em;
`;

interface DetailLayoutProps {
  title: string;
  images: readonly string[];
  backLink: string;
  children: ReactNode;
}

export default function DetailLayout({ title, images, backLink, children }: Readonly<DetailLayoutProps>) {
  return (
    <>
      <PageTitle title={title} />
      <Layout>
        <ColWrapper>
          <SingleBox>
            <ImageGallery images={images} alt={title} maxHeight={400} />
          </SingleBox>
          <Column>
            <BackLink link={backLink} />
            <Title>{title}</Title>
            {children}
          </Column>
        </ColWrapper>
      </Layout>
    </>
  );
}
