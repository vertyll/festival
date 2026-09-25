import { useCallback, useEffect, useRef, useState } from "react";

export type Loaded<T> = { status: "loading" } | { status: "loaded"; data: T } | { status: "error"; error: unknown };

export interface Loader<T> {
  state: Loaded<T>;
  data: T | null;
  reload: () => void;
}

export function useLoader<T>(load: () => Promise<T>, enabled = true): Loader<T> {
  const latestLoad = useRef(load);
  const [state, setState] = useState<Loaded<T>>({ status: "loading" });
  const [version, setVersion] = useState(0);

  useEffect(() => {
    latestLoad.current = load;
  });

  useEffect(() => {
    if (!enabled) {
      return undefined;
    }
    let active = true;
    latestLoad.current().then(
      (data) => active && setState({ status: "loaded", data }),
      (error: unknown) => active && setState({ status: "error", error })
    );
    return () => {
      active = false;
    };
  }, [enabled, version]);

  const reload = useCallback(() => setVersion((current) => current + 1), []);
  return { state, data: state.status === "loaded" ? state.data : null, reload };
}
