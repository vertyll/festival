import type { IsoDate } from "../api/types";

const NOON_UTC = 12;

export function dateOnly(value: IsoDate): Date {
  const [year, month, day] = value.split("-").map(Number);
  if (year === undefined || month === undefined || day === undefined) {
    throw new Error(`Invalid date: ${value}`);
  }
  return new Date(Date.UTC(year, month - 1, day, NOON_UTC));
}
