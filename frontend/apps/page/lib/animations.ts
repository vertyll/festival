import type { Transition, Variants } from "framer-motion";

export const pageVariants: Variants = {
  initial: { opacity: 0 },
  in: { opacity: 1 },
  out: { opacity: 0 },
};

export const pageTransition: Transition = {
  type: "tween",
  ease: "anticipate",
  duration: 0.2,
};
