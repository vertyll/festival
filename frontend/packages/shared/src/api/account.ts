import type { HttpClient } from "./http";
import type { Id, Order, PlaceOrderRequest, ProductCard, ShippingDetails } from "./types";

export function accountApi(http: HttpClient) {
  return {
    address: () => http.getOptional<ShippingDetails>("/api/account/address"),
    saveAddress: (details: ShippingDetails) => http.put<ShippingDetails>("/api/account/address", details),
    wishlist: () => http.get<ProductCard[]>("/api/account/wishlist"),
    wishlistProductIds: () => http.get<Id[]>("/api/account/wishlist/product-ids"),
    addToWishlist: (productId: Id) => http.send("PUT", `/api/account/wishlist/${productId}`),
    removeFromWishlist: (productId: Id) => http.send("DELETE", `/api/account/wishlist/${productId}`),
    orders: () => http.get<Order[]>("/api/account/orders"),
    placeOrder: (request: PlaceOrderRequest) => http.post<Order>("/api/orders", request),
  };
}
