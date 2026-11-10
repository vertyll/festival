import { Almendra, Roboto } from "next/font/google";

// Downloaded when the application is built and served from its own origin, so the page asks no other host for them.

/** The typeface of headings. */
export const almendra = Almendra({ weight: "400", subsets: ["latin", "latin-ext"], display: "swap" });

/** The typeface of text. */
export const roboto = Roboto({ weight: "400", subsets: ["latin", "latin-ext"], display: "swap" });
