import type { ReactNode } from "react";
import type { Loaded } from "./useLoader";

interface LoadStateProps<T> {
  state: Loaded<T>;
  loading: ReactNode;
  failed: ReactNode;
  children: (data: T) => ReactNode;
}

export function LoadState<T>({ state, loading, failed, children }: LoadStateProps<T>) {
  switch (state.status) {
    case "loading":
      return loading;
    case "error":
      return failed;
    case "loaded":
      return children(state.data);
  }
}
