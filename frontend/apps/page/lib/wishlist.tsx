import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from "react";
import { useSession } from "@festival/shared/auth/session";
import type { Id } from "@festival/shared/api/types";
import { account } from "./api";

interface WishlistContextValue {
  productIds: ReadonlySet<Id>;
  toggle: (productId: Id) => Promise<void>;
}

const WishlistContext = createContext<WishlistContextValue | null>(null);

const NONE: ReadonlySet<Id> = new Set();

export function WishlistProvider({ children }: Readonly<{ children: ReactNode }>) {
  const { session } = useSession();
  const [productIds, setProductIds] = useState<ReadonlySet<Id>>(NONE);
  const authenticated = session.status === "authenticated";

  useEffect(() => {
    if (!authenticated) {
      return undefined;
    }
    let active = true;
    void account.wishlistProductIds().then((ids) => {
      if (active) {
        setProductIds(new Set(ids));
      }
    });
    return () => {
      active = false;
    };
  }, [authenticated]);

  const toggle = useCallback(
    async (productId: Id) => {
      const wished = productIds.has(productId);
      const update = (add: boolean) =>
        setProductIds((current) => {
          const next = new Set(current);
          if (add) {
            next.add(productId);
          } else {
            next.delete(productId);
          }
          return next;
        });
      update(!wished);
      try {
        await (wished ? account.removeFromWishlist(productId) : account.addToWishlist(productId));
      } catch (error) {
        update(wished);
        throw error;
      }
    },
    [productIds]
  );

  const visibleIds = authenticated ? productIds : NONE;
  const value = useMemo(() => ({ productIds: visibleIds, toggle }), [visibleIds, toggle]);
  return <WishlistContext.Provider value={value}>{children}</WishlistContext.Provider>;
}

export function useWishlist(): WishlistContextValue {
  const value = useContext(WishlistContext);
  if (!value) {
    throw new Error("useWishlist() must be used within <WishlistProvider>");
  }
  return value;
}
