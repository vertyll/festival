import lottie, { type AnimationItem } from "lottie-web";
import { useEffect, useRef, type CSSProperties } from "react";

const ANIMATIONS = {
  cart: "/lottie/sammy-shopping.json",
  login: "/lottie/isometric-face-id-technology-on-phone.json",
  search: "/lottie/isometric-research-of-statistical-data-and-analytics.json",
  thanks: "/lottie/florid-web-wallet-and-online-banking.json",
} as const;

export type AnimationName = keyof typeof ANIMATIONS;

interface LottieAnimationProps {
  name: AnimationName;
  style?: CSSProperties;
}

const DEFAULT_STYLE: CSSProperties = { maxWidth: "200px", height: "200px" };

export default function LottieAnimation({ name, style = DEFAULT_STYLE }: Readonly<LottieAnimationProps>) {
  const container = useRef<HTMLDivElement>(null);

  useEffect(() => {
    let animation: AnimationItem | null = null;
    let active = true;
    void fetch(ANIMATIONS[name])
      .then((response) => response.json())
      .then((animationData: unknown) => {
        if (active && container.current) {
          animation = lottie.loadAnimation({
            container: container.current,
            renderer: "svg",
            loop: true,
            autoplay: true,
            animationData,
            rendererSettings: { preserveAspectRatio: "xMidYMid meet" },
          });
        }
      });
    return () => {
      active = false;
      animation?.destroy();
    };
  }, [name]);

  return <div ref={container} style={style} />;
}
