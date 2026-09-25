import type { ButtonHTMLAttributes } from "react";

export type ButtonVariant = "primary" | "danger" | "login";

const VARIANTS: Record<ButtonVariant, string> = {
  primary: "btn-primary",
  danger: "btn-danger",
  login: "btn-login",
};

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
}

export default function Button({
  variant = "primary",
  type = "button",
  className = "",
  ...props
}: Readonly<ButtonProps>) {
  return <button type={type} className={`${VARIANTS[variant]} ${className}`} {...props} />;
}
