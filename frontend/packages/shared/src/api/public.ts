import type { HttpClient } from "./http";
import type {
  Artist,
  FlatMessages,
  Language,
  ArtistDetails,
  Id,
  MeResponse,
  News,
  ProductCard,
  ProductDetails,
  ProductSearch,
  ShopSettings,
  Sponsor,
} from "./types";

export function publicApi(http: HttpClient) {
  return {
    me: () => http.get<MeResponse>("/api/me"),
    translations: (language: Language) => http.get<FlatMessages>(`/api/i18n/${language}`),
    settings: () => http.get<ShopSettings>("/api/settings"),
    products: (search: ProductSearch = {}) => http.get<ProductCard[]>("/api/products", { ...search }),
    product: (id: Id) => http.get<ProductDetails>(`/api/products/${id}`),
    artists: (limit?: number) => http.get<Artist[]>("/api/artists", { limit }),
    artist: (id: Id) => http.get<ArtistDetails>(`/api/artists/${id}`),
    newsList: (limit?: number) => http.get<News[]>("/api/news", { limit }),
    news: (id: Id) => http.get<News>(`/api/news/${id}`),
    sponsors: () => http.get<Sponsor[]>("/api/sponsors"),
  };
}

export type PublicApi = ReturnType<typeof publicApi>;
