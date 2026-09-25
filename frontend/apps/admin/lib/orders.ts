import type { Order } from "@festival/shared/api/types";

const DAY_MS = 24 * 60 * 60 * 1000;

export function recentOrders(orders: readonly Order[], days: number, now: number): Order[] {
  return orders.filter((order) => order.status !== "CANCELLED" && now - Date.parse(order.createdAt) < days * DAY_MS);
}

export function revenue(orders: readonly Order[]): number {
  return orders.reduce((sum, order) => sum + order.total, 0);
}
