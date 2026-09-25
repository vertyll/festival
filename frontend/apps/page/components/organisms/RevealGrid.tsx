import type { ReactNode } from "react";
import styled from "styled-components";
import RevealWrapper from "../atoms/RevealWrapper";

export type Columns = Readonly<Record<number, number>>;

const Grid = styled.div<{ $columns: Columns; $gap: number }>`
  display: grid;
  gap: ${(props) => props.$gap}px;
  padding-top: 50px;
  justify-items: center;
  ${(props) =>
    Object.entries(props.$columns)
      .map(([width, count]) => {
        const rule = `grid-template-columns: repeat(${count}, 1fr);`;
        return Number(width) === 0 ? rule : `@media screen and (min-width: ${width}px) { ${rule} }`;
      })
      .join("\n")}
`;

interface RevealGridProps<T> {
  items: readonly T[];
  itemKey: (item: T) => string;
  columns: Columns;
  gap: number;
  children: (item: T) => ReactNode;
}

export default function RevealGrid<T>({ items, itemKey, columns, gap, children }: Readonly<RevealGridProps<T>>) {
  return (
    <Grid $columns={columns} $gap={gap}>
      {items.map((item, index) => (
        <RevealWrapper key={itemKey(item)} delay={index * 50}>
          {children(item)}
        </RevealWrapper>
      ))}
    </Grid>
  );
}

export const WIDE_GRID: Columns = { 0: 1, 575: 2, 768: 3, 1100: 4 };
