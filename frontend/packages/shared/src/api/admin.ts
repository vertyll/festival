import type { HttpClient } from "./http";
import type {
  AdminProduct,
  Administrator,
  AdministratorRequest,
  Artist,
  ArtistRequest,
  Attribute,
  AttributeRequest,
  Category,
  CategoryRequest,
  Id,
  News,
  NewsRequest,
  Order,
  ProductRequest,
  ShopSettings,
  ShopSettingsRequest,
  Sponsor,
  SponsorRequest,
  Stage,
  StageRequest,
  Translation,
  TranslationImportResult,
  TranslationRequest,
  UploadResponse,
} from "./types";

function collection<T, R>(http: HttpClient, base: string) {
  return {
    list: () => http.get<T[]>(base),
    create: (request: R) => http.post<T>(base, request),
    update: (id: Id, request: R) => http.put<T>(`${base}/${id}`, request),
    remove: (id: Id) => http.send("DELETE", `${base}/${id}`),
  };
}

function editableCollection<T, R>(http: HttpClient, base: string) {
  return { ...collection<T, R>(http, base), get: (id: Id) => http.get<T>(`${base}/${id}`) };
}

export function adminApi(http: HttpClient) {
  return {
    stages: collection<Stage, StageRequest>(http, "/api/admin/stages"),
    artists: editableCollection<Artist, ArtistRequest>(http, "/api/admin/artists"),
    news: editableCollection<News, NewsRequest>(http, "/api/admin/news"),
    sponsors: editableCollection<Sponsor, SponsorRequest>(http, "/api/admin/sponsors"),
    categories: collection<Category, CategoryRequest>(http, "/api/admin/categories"),
    attributes: collection<Attribute, AttributeRequest>(http, "/api/admin/attributes"),
    products: editableCollection<AdminProduct, ProductRequest>(http, "/api/admin/products"),
    administrators: collection<Administrator, AdministratorRequest>(http, "/api/admin/administrators"),
    orders: { list: () => http.get<Order[]>("/api/admin/orders") },
    settings: {
      get: () => http.get<ShopSettings>("/api/admin/settings"),
      update: (request: ShopSettingsRequest) => http.put<ShopSettings>("/api/admin/settings", request),
    },
    translations: {
      list: () => http.get<Translation[]>("/api/admin/translations"),
      update: (key: string, request: TranslationRequest) =>
        http.put<Translation>(`/api/admin/translations/${encodeURIComponent(key)}`, request),
      reset: (key: string) => http.send("DELETE", `/api/admin/translations/${encodeURIComponent(key)}/customization`),
      exportUrl: "/api/admin/translations/export",
      importSpreadsheet(file: File) {
        const form = new FormData();
        form.append("file", file);
        return http.upload<TranslationImportResult>("/api/admin/translations/import", form);
      },
    },
    media: {
      upload(files: readonly File[]) {
        const form = new FormData();
        files.forEach((file) => form.append("files", file));
        return http.upload<UploadResponse>("/api/admin/media", form);
      },
    },
  };
}
