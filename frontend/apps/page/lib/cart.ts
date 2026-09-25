import { useSyncExternalStore } from "react";
import type { Id, Message, SelectedOption, SelectedOptions } from "@festival/shared/api/types";
import { LIMITS, message } from "@festival/shared/validation/validation";

export interface CartItem {
  productId: Id;
  selectedOptions: SelectedOptions;
  selection: SelectedOption[];
  quantity: number;
}

export type CartEntry = Omit<CartItem, "quantity">;

const STORAGE_KEY = "festival-cart-v2";
const EMPTY: readonly CartItem[] = [];

const listeners = new Set<() => void>();
let snapshot: readonly CartItem[] | null = null;

function read(): readonly CartItem[] {
  snapshot ??= JSON.parse(localStorage.getItem(STORAGE_KEY) ?? "[]") as CartItem[];
  return snapshot;
}

function write(items: readonly CartItem[]): void {
  snapshot = items;
  if (items.length === 0) {
    localStorage.removeItem(STORAGE_KEY);
  } else {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
  }
  listeners.forEach((listener) => listener());
}

function subscribe(listener: () => void): () => void {
  const onStorage = (event: StorageEvent) => {
    if (event.key === STORAGE_KEY) {
      snapshot = null;
      listener();
    }
  };
  listeners.add(listener);
  window.addEventListener("storage", onStorage);
  return () => {
    listeners.delete(listener);
    window.removeEventListener("storage", onStorage);
  };
}

export function cartItemKey(productId: Id, selectedOptions: SelectedOptions): string {
  const options = Object.entries(selectedOptions).sort(([a], [b]) => a.localeCompare(b));
  return `${productId}:${JSON.stringify(options)}`;
}

function find(items: readonly CartItem[], entry: CartEntry): CartItem | undefined {
  const key = cartItemKey(entry.productId, entry.selectedOptions);
  return items.find((item) => cartItemKey(item.productId, item.selectedOptions) === key);
}

export function canIncrease(item: CartItem): boolean {
  return item.quantity < LIMITS.maxQuantity;
}

function add(entry: CartEntry): Message | null {
  const items = read();
  const existing = find(items, entry);
  if (existing) {
    if (!canIncrease(existing)) {
      return message("validation.quantityTooLarge", { value: LIMITS.maxQuantity });
    }
    write(items.map((item) => (item === existing ? { ...item, quantity: item.quantity + 1 } : item)));
    return null;
  }
  if (items.length >= LIMITS.maxCartLines) {
    return message("validation.cartTooManyLines", { max: LIMITS.maxCartLines });
  }
  write([...items, { ...entry, quantity: 1 }]);
  return null;
}

function remove(entry: CartEntry): void {
  const items = read();
  const existing = find(items, entry);
  if (!existing) {
    return;
  }
  write(
    existing.quantity > 1
      ? items.map((item) => (item === existing ? { ...item, quantity: item.quantity - 1 } : item))
      : items.filter((item) => item !== existing)
  );
}

export const cart = {
  add,
  remove,
  clear: () => write(EMPTY),
  retain: (productIds: ReadonlySet<Id>) => {
    const items = read();
    const kept = items.filter((item) => productIds.has(item.productId));
    if (kept.length !== items.length) {
      write(kept);
    }
  },
};

export function useCartItems(): readonly CartItem[] {
  return useSyncExternalStore(subscribe, read, () => EMPTY);
}
