import styled from "styled-components";

const StyledTabs = styled.div`
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;

  @media screen and (min-width: 1200px) {
    flex-direction: row;
    gap: 20px;
    margin: 0 35px;
  }
`;

const Tab = styled.button<{ $active: boolean }>`
  font-size: 1.4rem;
  cursor: pointer;
  background: none;
  border: 0;
  padding: 0;
  text-align: left;
  color: ${(props) => (props.$active ? "black" : "var(--gray-color)")};
  border-bottom: 2px solid ${(props) => (props.$active ? "var(--dark-text-color)" : "transparent")};
`;

interface TabsProps<T extends string> {
  tabs: Readonly<Record<T, string>>;
  active: T;
  onChange: (tab: T) => void;
}

export default function Tabs<T extends string>({ tabs, active, onChange }: Readonly<TabsProps<T>>) {
  return (
    <StyledTabs role="tablist">
      {(Object.keys(tabs) as T[]).map((tab) => (
        <Tab
          key={tab}
          type="button"
          role="tab"
          aria-selected={tab === active}
          $active={tab === active}
          onClick={() => onChange(tab)}
        >
          {tabs[tab]}
        </Tab>
      ))}
    </StyledTabs>
  );
}
