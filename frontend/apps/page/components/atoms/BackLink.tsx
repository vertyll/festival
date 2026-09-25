import Link from "next/link";
import styled from "styled-components";
import { useTranslations } from "use-intl";

const Back = styled(Link)`
  margin-top: 15px;
  font-weight: bold;
  font-size: 0.9em;
  text-decoration: none;
  color: var(--dark-text-color);
`;

export default function BackLink({ link }: Readonly<{ link: string }>) {
  const t = useTranslations("common");
  return <Back href={link}>&#8592; {t("back")}</Back>;
}
