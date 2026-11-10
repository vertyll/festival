import type { ReactNode } from "react";
import styled, { keyframes } from "styled-components";
import { pageEnterTiming, pageTransition, pageVariants } from "@/lib/animations";
import { AnimatedPage } from "../atoms/AnimatedPage";
import CookieBanner from "../organisms/CookieBanner";
import Footer from "../organisms/Footer";
import Header from "../organisms/Header";

const Page = styled.div`
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  min-height: 100dvh;
`;

const fadeIn = keyframes`
  from {
    opacity: 0;
  }
`;

const Content = styled(AnimatedPage)`
  flex: 1;
  animation: ${fadeIn} ${pageEnterTiming};
`;

const Main = styled.main`
  min-height: 80vh;
  margin-bottom: 50px;
`;

export default function Layout({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <Page>
      <Header />
      <Content exit="out" variants={pageVariants} transition={pageTransition}>
        <Main>{children}</Main>
      </Content>
      <CookieBanner />
      <Footer />
    </Page>
  );
}
