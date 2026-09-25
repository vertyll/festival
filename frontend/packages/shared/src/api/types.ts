export type Id = string;

export type IsoDateTime = string;

export type IsoDate = string;

export type LocalTime = string;

export const LANGUAGES = ["pl", "en"] as const;
export type Language = (typeof LANGUAGES)[number];

export type LocalizedText = Readonly<Record<Language, string>>;

export interface Reference {
  id: Id;
  name: LocalizedText;
}

export type MessageArg = string | number | LocalizedText;
export type MessageArgs = Readonly<Record<string, MessageArg>>;

export interface Message {
  code: string;
  args: MessageArgs;
}

export interface ProblemDetail extends Message {
  type: string;
  title: string;
  status: number;
  instance: string | null;
  errors?: Record<string, Message>;
}

export interface SessionUser {
  email: string;
  name: string | null;
  picture: string | null;
  administrator: boolean;
}

export interface MeResponse {
  user: SessionUser | null;
}

export interface Stage {
  id: Id;
  name: LocalizedText;
}

export interface StageRequest {
  name: LocalizedText;
}

export interface Artist {
  id: Id;
  name: string;
  description: LocalizedText | null;
  images: string[];
  stageId: Id | null;
  concertDate: IsoDate | null;
  concertTime: LocalTime | null;
  createdAt: IsoDateTime;
  updatedAt: IsoDateTime;
}

export interface ArtistDetails {
  id: Id;
  name: string;
  description: LocalizedText | null;
  images: string[];
  stage: Reference | null;
  concertDate: IsoDate | null;
  concertTime: LocalTime | null;
}

export interface ArtistRequest {
  name: string;
  description: LocalizedText | null;
  images: string[];
  stageId: Id | null;
  concertDate: IsoDate | null;
  concertTime: LocalTime | null;
}

export interface News {
  id: Id;
  name: LocalizedText;
  description: LocalizedText;
  images: string[];
  createdAt: IsoDateTime;
  updatedAt: IsoDateTime;
}

export interface NewsRequest {
  name: LocalizedText;
  description: LocalizedText;
  images: string[];
}

export interface Sponsor {
  id: Id;
  name: string;
  link: string;
  images: string[];
  createdAt: IsoDateTime;
  updatedAt: IsoDateTime;
}

export interface SponsorRequest {
  name: string;
  link: string;
  images: string[];
}

export interface Category {
  id: Id;
  name: LocalizedText;
  parent: Reference | null;
}

export interface CategoryRequest {
  name: LocalizedText;
  parentId: Id | null;
}

export interface OptionValue {
  code: string;
  label: LocalizedText;
}

export interface Attribute {
  id: Id;
  name: LocalizedText;
  values: OptionValue[];
}

export interface AttributeRequest {
  name: LocalizedText;
  values: OptionValue[];
}

export interface ProductOption {
  code: string;
  name: LocalizedText;
  values: OptionValue[];
}

export interface ProductVariant {
  valueCodes: string[];
  stock: number;
}

export interface ProductCard {
  id: Id;
  name: LocalizedText;
  price: number;
  images: string[];
}

export interface VariantAvailability {
  valueCodes: string[];
  available: boolean;
  stock: number | null;
}

export interface ProductDetails {
  id: Id;
  name: LocalizedText;
  description: LocalizedText | null;
  price: number;
  images: string[];
  options: ProductOption[];
  variants: VariantAvailability[];
  totalStock: number | null;
  categoryPath: Reference[];
}

export interface AdminProduct {
  id: Id;
  name: LocalizedText;
  description: LocalizedText | null;
  price: number;
  categoryId: Id | null;
  images: string[];
  options: ProductOption[];
  variants: ProductVariant[];
  createdAt: IsoDateTime;
  updatedAt: IsoDateTime;
}

export interface ProductRequest {
  name: LocalizedText;
  description: LocalizedText | null;
  price: number;
  categoryId: Id | null;
  images: string[];
  options: ProductOption[];
  variants: ProductVariant[];
}

export const PRODUCT_SORTS = ["NEWEST", "PRICE_ASC", "PRICE_DESC"] as const;
export type ProductSort = (typeof PRODUCT_SORTS)[number];

export interface ProductSearch {
  term?: string;
  ids?: Id[];
  sort?: ProductSort;
  limit?: number;
}

export interface ShopSettings {
  shippingPrice: number;
  stockVisible: boolean;
  variantStockVisible: boolean;
  checkoutEnabled: boolean;
  currency: string;
}

export interface ShopSettingsRequest {
  shippingPrice: number;
  stockVisible: boolean;
  variantStockVisible: boolean;
}

export interface ShippingDetails {
  name: string;
  email: string;
  streetAddress: string;
  postalCode: string;
  city: string;
  country: string;
}

export const ORDER_STATUSES = ["AWAITING_PAYMENT", "PAID", "CANCELLED"] as const;
export type OrderStatus = (typeof ORDER_STATUSES)[number];

export interface SelectedOption {
  option: LocalizedText;
  value: LocalizedText;
}

export interface OrderLine {
  productId: Id;
  productName: LocalizedText;
  valueCodes: string[];
  selection: SelectedOption[];
  unitPrice: number;
  quantity: number;
  lineTotal: number;
}

export interface Order {
  id: Id;
  status: OrderStatus;
  lines: OrderLine[];
  shipping: ShippingDetails;
  itemsTotal: number;
  shippingPrice: number;
  total: number;
  currency: string;
  createdAt: IsoDateTime;
}

export type SelectedOptions = Record<string, string>;

export interface OrderItemRequest {
  productId: Id;
  selectedOptions: SelectedOptions;
  quantity: number;
}

export interface PlaceOrderRequest {
  shipping: ShippingDetails;
  items: OrderItemRequest[];
}

export interface Administrator {
  id: Id;
  email: string;
  createdAt: IsoDateTime;
}

export interface AdministratorRequest {
  email: string;
}

export interface UploadResponse {
  urls: string[];
}

export interface Translation {
  key: string;
  messages: LocalizedText;
  defaults: LocalizedText;
  customized: boolean;
  updatedAt: IsoDateTime;
}

export interface TranslationImportResult {
  updated: number;
  unchanged: number;
}

export interface TranslationRequest {
  messages: LocalizedText;
}

export type FlatMessages = Readonly<Record<string, string>>;
