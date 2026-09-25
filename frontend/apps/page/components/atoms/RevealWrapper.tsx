import { useEffect, useRef, useState, type ReactNode } from "react";
import styled from "styled-components";

const Wrapper = styled.div<{ $isVisible: boolean; $delay: number }>`
  opacity: ${(props) => (props.$isVisible ? 1 : 0)};
  transform: translateY(${(props) => (props.$isVisible ? "0" : "20px")});
  transition:
    opacity 0.6s ease-out,
    transform 0.6s ease-out;
  transition-delay: ${(props) => props.$delay}ms;
`;

export default function RevealWrapper({ children, delay = 0 }: Readonly<{ children: ReactNode; delay?: number }>) {
  const [isVisible, setIsVisible] = useState(false);
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const element = ref.current;
    if (!element) {
      return undefined;
    }
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry?.isIntersecting) {
          setIsVisible(true);
          observer.disconnect();
        }
      },
      { threshold: 0.1, rootMargin: "50px" }
    );
    observer.observe(element);
    return () => observer.disconnect();
  }, []);

  return (
    <Wrapper ref={ref} $isVisible={isVisible} $delay={delay}>
      {children}
    </Wrapper>
  );
}
