import styled, { keyframes } from "styled-components";
import { useTranslations } from "use-intl";

const spin = keyframes`
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
`;

const Wrapper = styled.div<{ $padding: string }>`
  display: flex;
  justify-content: center;
  align-items: center;
  padding: ${(props) => props.$padding};
`;

const StyledSpinner = styled.div<{ $size: string; $borderWidth: string }>`
  border: ${(props) => props.$borderWidth} solid var(--main-medium-slate-blue-color);
  border-top: ${(props) => props.$borderWidth} solid var(--nav-color);
  border-radius: 50%;
  width: ${(props) => props.$size};
  height: ${(props) => props.$size};
  animation: ${spin} 0.6s linear infinite;
`;

interface SpinnerProps {
  size?: string;
  borderWidth?: string;
  padding?: string;
}

export default function Spinner({ size = "4em", borderWidth = "0.7em", padding = "20px 0" }: Readonly<SpinnerProps>) {
  const t = useTranslations("common");
  return (
    <Wrapper $padding={padding} role="status" aria-label={t("loading")}>
      <StyledSpinner $size={size} $borderWidth={borderWidth} />
    </Wrapper>
  );
}
