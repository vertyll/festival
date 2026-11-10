import { anticipate, type Transition, type Variants } from "framer-motion";

const PAGE_TRANSITION_SECONDS = 0.2;
const EASING_SAMPLES = PAGE_TRANSITION_SECONDS * 100;

/** How a page leaves; it enters by a CSS animation, as server-rendered HTML may carry no `style` attribute. */
export const pageVariants: Variants = {
  out: { opacity: 0 },
};

export const pageTransition: Transition = {
  type: "tween",
  ease: "anticipate",
  duration: PAGE_TRANSITION_SECONDS,
};

const easingPoints = Array.from(
  { length: EASING_SAMPLES },
  (_, index) => Math.round(anticipate(index / (EASING_SAMPLES - 1)) * 10000) / 10000
);

/**
 * The page transition as the timing of a CSS `animation`: its duration, and its easing as a `linear()` function sampled
 * every 10 ms, as Motion samples an easing it hands to the browser.
 */
export const pageEnterTiming = `${PAGE_TRANSITION_SECONDS}s linear(${easingPoints.join(", ")})`;
