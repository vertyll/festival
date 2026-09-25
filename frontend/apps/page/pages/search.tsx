import { useTranslations } from "use-intl";
import { useEffect, useState } from "react";
import styled from "styled-components";
import type { ProductCard } from "@festival/shared/api/types";
import { useDebouncedValue } from "@festival/shared/react/useDebouncedValue";
import DivCenter from "@/components/atoms/DivCenter";
import PageTitle from "@/components/atoms/PageTitle";
import Input from "@/components/atoms/Input";
import LottieAnimation from "@/components/atoms/LottieAnimation";
import Spinner from "@/components/atoms/Spinner";
import ProductBox from "@/components/organisms/ProductBox";
import RevealGrid, { WIDE_GRID } from "@/components/organisms/RevealGrid";
import Layout from "@/components/templates/Layout";
import { api } from "@/lib/api";

const SearchWrapper = styled.div`
  margin-top: 50px;
  max-width: 980px;
  width: 100%;
`;

interface SearchResult {
  term: string;
  products: ProductCard[];
}

export default function SearchPage() {
  const t = useTranslations("page.search");
  const [input, setInput] = useState("");
  const term = useDebouncedValue(input.trim(), 500);
  const [result, setResult] = useState<SearchResult | null>(null);

  useEffect(() => {
    if (term === "") {
      return undefined;
    }
    let active = true;
    void api.products({ term }).then((products) => {
      if (active) {
        setResult({ term, products });
      }
    });
    return () => {
      active = false;
    };
  }, [term]);

  const typed = input.trim();
  const loading = typed !== "" && (typed !== term || result?.term !== term);
  const products = typed !== "" && !loading && result ? result.products : [];

  return (
    <>
      <PageTitle title={t("title")} />
      <Layout>
        <DivCenter>
          <SearchWrapper>
            <Input
              autoFocus
              value={input}
              onChange={(event) => setInput(event.target.value)}
              placeholder={t("placeholder")}
              aria-label={t("label")}
            />
          </SearchWrapper>
          {loading && <Spinner />}
          {!loading && typed !== "" && products.length === 0 && <h2>{t("noResults", { term: typed })}</h2>}
          {products.length > 0 ? (
            <RevealGrid items={products} itemKey={(product) => product.id} columns={WIDE_GRID} gap={50}>
              {(product) => <ProductBox product={product} />}
            </RevealGrid>
          ) : (
            !loading && <LottieAnimation name="search" style={{ maxWidth: "450px", height: "450px" }} />
          )}
        </DivCenter>
      </Layout>
    </>
  );
}
