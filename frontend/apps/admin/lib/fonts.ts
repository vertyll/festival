import { Roboto } from "next/font/google";

// Downloaded when the application is built and served from its own origin, so the panel asks no other host for it.

/** The typeface of the panel. */
export const roboto = Roboto({ weight: "400", subsets: ["latin", "latin-ext"], display: "swap" });
