import { useEffect, useRef } from "react";
import styled from "styled-components";
import { useTranslations } from "use-intl";

export type AlertType = "success" | "danger" | "default";

const BACKGROUNDS: Record<AlertType, string> = {
  success: "var(--alert-success-color)",
  danger: "var(--alert-danger-color)",
  default: "var(--alert-default-color)",
};

const AlertWrapper = styled.div<{ $type: AlertType }>`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background-color: ${(props) => BACKGROUNDS[props.$type]};
  color: var(--dark-text-color);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px 20px;
  z-index: 1000;
`;

const CloseButton = styled.button`
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  border: none;
  background: none;
  color: var(--dark-text-color);
  font-size: 20px;
  cursor: pointer;
`;

interface AlertProps {
  message: string;
  onClose: () => void;
  type?: AlertType;
  duration?: number;
}

export function Alert({ message, onClose, type = "default", duration = 2000 }: Readonly<AlertProps>) {
  const t = useTranslations("common");
  const latestOnClose = useRef(onClose);

  useEffect(() => {
    latestOnClose.current = onClose;
  });

  useEffect(() => {
    const timer = setTimeout(() => latestOnClose.current(), duration);
    return () => clearTimeout(timer);
  }, [duration]);

  return (
    <AlertWrapper $type={type} role="alert">
      {message}
      <CloseButton onClick={onClose} aria-label={t("close")}>
        &times;
      </CloseButton>
    </AlertWrapper>
  );
}
