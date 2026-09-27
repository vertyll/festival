import type { ReactNode } from "react";

export function bold(chunks: ReactNode) {
  return <b>{chunks}</b>;
}

export function strong(chunks: ReactNode) {
  return <strong>{chunks}</strong>;
}
