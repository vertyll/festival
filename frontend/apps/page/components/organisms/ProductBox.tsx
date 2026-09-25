import Image from "next/image";
import Link from "next/link";
import { useState, type MouseEvent } from "react";
import styled from "styled-components";
import type { ProductCard } from "@festival/shared/api/types";
import { useSession } from "@festival/shared/auth/session";
import { useWishlist } from "@/lib/wishlist";
import { Alert } from "../atoms/Alert";
import HoverText from "../atoms/HoverText";
import Price from "../atoms/Price";
import { IconHeart, IconHeartOutline } from "../atoms/icons";
import { useTranslations } from "use-intl";
import { useLocalized } from "@festival/shared/i18n/IntlSetup";

const Box = styled(Link)`
  background-color: var(--main-white-smoke-color);
  padding: 30px 10px;
  height: 150px;
  width: 200px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  box-shadow: var(--default-box-shadow);

  &:hover ${HoverText} {
    display: block;
  }
`;

const WishlistButton = styled.button<{ $wished: boolean }>`
  border: 0;
  width: 40px;
  height: 40px;
  padding: 10px;
  position: absolute;
  top: 0;
  right: 0;
  background: transparent;
  cursor: pointer;
  z-index: 1;
  color: ${(props) => (props.$wished ? "red" : "black")};
  svg {
    width: 16px;
  }
`;

const Name = styled(Link)`
  text-decoration: none;
  color: inherit;
`;

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
`;

const ProductInfo = styled.div`
  margin-top: 10px;
`;

export default function ProductBox({ product }: Readonly<{ product: ProductCard }>) {
  const t = useTranslations("page.productBox");
  const localized = useLocalized();
  const { session } = useSession();
  const wishlist = useWishlist();
  const [alert, setAlert] = useState<string | null>(null);
  const url = `/product/${product.id}`;
  const wished = wishlist.productIds.has(product.id);

  function toggleWishlist(event: MouseEvent<HTMLButtonElement>) {
    event.preventDefault();
    event.stopPropagation();
    if (session.status !== "authenticated") {
      setAlert(t("loginRequired"));
      return;
    }
    wishlist.toggle(product.id).catch(() => setAlert(t("wishlistFailed")));
  }

  return (
    <Wrapper>
      {alert && <Alert message={alert} onClose={() => setAlert(null)} duration={3000} type="danger" />}
      <Box href={url}>
        <WishlistButton
          type="button"
          $wished={wished}
          aria-label={wished ? t("removeFromWishlist") : t("addToWishlist")}
          onClick={toggleWishlist}
        >
          {wished ? <IconHeart /> : <IconHeartOutline />}
        </WishlistButton>
        <Image
          src={product.images[0] ?? "/images/no-image-found.webp"}
          alt=""
          fill
          sizes="200px"
          style={{ objectFit: "cover" }}
        />
        <HoverText>{t("see")} &#8594;</HoverText>
      </Box>
      <ProductInfo>
        <Name href={url}>{localized(product.name)}</Name>
        <p>
          {t("price")}{" "}
          <b>
            <Price amount={product.price} />
          </b>
        </p>
      </ProductInfo>
    </Wrapper>
  );
}
