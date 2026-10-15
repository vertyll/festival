import styled from "styled-components";
import FooterInfoLinks from "../molecules/FooterInfoLinks";

const StyledFooter = styled.footer`
  background-color: var(--footer-color);
  color: var(--light-text-color);
  padding: 24px 20px;
`;

const Wrapper = styled.div`
  max-width: 1200px;
  margin: 0 auto;
`;

export default function Footer() {
  return (
    <StyledFooter>
      <Wrapper>
        <FooterInfoLinks />
      </Wrapper>
    </StyledFooter>
  );
}
