import type { ButtonHTMLAttributes } from "react";
import styled, { css } from "styled-components";

export type ButtonUsage = "primary" | "danger";
export type ButtonSize = "s" | "m" | "l";

interface StyleProps {
  $usage?: ButtonUsage;
  $size?: ButtonSize;
}

const USAGES: Record<ButtonUsage, ReturnType<typeof css>> = {
  primary: css`
    background-color: var(--main-maize-color);
    color: var(--dark-text-color);
  `,
  danger: css`
    background-color: var(--main-danger-color);
  `,
};

const SIZES: Record<ButtonSize, ReturnType<typeof css>> = {
  l: css`
    padding: 15px 35px;
    font-size: 1.2rem;
  `,
  m: css`
    padding: 10px 25px;
    font-size: 1rem;
  `,
  s: css`
    padding: 4px 14px;
    font-size: 1rem;
  `,
};

const StyledButton = styled.button<StyleProps>`
  border: 0;
  width: 100%;
  max-width: max-content;
  border-radius: 30px;
  padding: 5px 15px;
  transition: 0.5s;
  display: inline-flex;
  align-items: center;
  cursor: pointer;
  svg {
    height: 24px;
    margin-right: 5px;
  }

  &:hover {
    filter: brightness(0.85);
  }

  &:disabled {
    cursor: not-allowed;
    filter: grayscale(0.6);
  }

  ${(props) => props.$usage && USAGES[props.$usage]}
  ${(props) => props.$size && SIZES[props.$size]}
`;

export type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & StyleProps;

export default function Button({ children, type = "button", ...props }: ButtonProps) {
  return (
    <StyledButton type={type} {...props}>
      {children}
    </StyledButton>
  );
}
