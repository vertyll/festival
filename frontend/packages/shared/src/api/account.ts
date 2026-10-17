import type { HttpClient } from "./http";
import type { Id, Language, Order, PlaceOrderRequest, ProductCard, SecondFactor, ShippingDetails } from "./types";

export function accountApi(http: HttpClient) {
  return {
    address: () => http.getOptional<ShippingDetails>("/api/account/address"),
    saveAddress: (details: ShippingDetails) => http.put<ShippingDetails>("/api/account/address", details),
    wishlist: () => http.get<ProductCard[]>("/api/account/wishlist"),
    wishlistProductIds: () => http.get<Id[]>("/api/account/wishlist/product-ids"),
    addToWishlist: (productId: Id) => http.send("PUT", `/api/account/wishlist/${productId}`),
    removeFromWishlist: (productId: Id) => http.send("DELETE", `/api/account/wishlist/${productId}`),
    orders: () => http.get<Order[]>("/api/account/orders"),
    secondFactor: () => http.get<SecondFactor>("/api/account/second-factor"),
    keepLanguage: (language: Language) => http.put<{ language: Language }>("/api/account/language", { language }),
    placeOrder: (request: PlaceOrderRequest) => http.post<Order>("/api/orders", request),
  };
}
