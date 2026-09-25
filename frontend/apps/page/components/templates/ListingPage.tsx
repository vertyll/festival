import type { ReactNode } from "react";
import DivCenter from "../atoms/DivCenter";
import Title from "../atoms/Title";
import TitleBanner from "../atoms/TitleBanner";
import PageTitle from "../atoms/PageTitle";
import Layout from "./Layout";

interface ListingPageProps {
  title: string;
  heading: string;
  bannerUrl?: string;
  emptyMessage: string | null;
  children: ReactNode;
}

export default function ListingPage({ title, heading, bannerUrl, emptyMessage, children }: Readonly<ListingPageProps>) {
  return (
    <>
      <PageTitle title={title} />
      <Layout>
        {bannerUrl && <TitleBanner imageUrl={bannerUrl} />}
        <DivCenter>
          <Title>{heading}</Title>
          {emptyMessage === null ? children : <p>{emptyMessage}</p>}
        </DivCenter>
      </Layout>
    </>
  );
}
