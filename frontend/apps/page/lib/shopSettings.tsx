import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import type { ShopSettings } from "@festival/shared/api/types";
import { api } from "./api";

const ShopSettingsContext = createContext<ShopSettings | null>(null);

export function ShopSettingsProvider({
  initial,
  children,
}: Readonly<{
  initial: ShopSettings | null;
  children: ReactNode;
}>) {
  const [settings, setSettings] = useState<ShopSettings | null>(initial);

  useEffect(() => {
    if (settings === null) {
      void api.settings().then(setSettings);
    }
  }, [settings]);

  return <ShopSettingsContext.Provider value={settings}>{children}</ShopSettingsContext.Provider>;
}

export function useShopSettings(): ShopSettings | null {
  return useContext(ShopSettingsContext);
}

export interface WithShopSettings {
  shopSettings: ShopSettings;
}
