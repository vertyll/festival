package com.vertyll.festival.shop.wishlist;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.vertyll.festival.catalog.product.ProductCardResponse;
import com.vertyll.festival.security.UserIdentity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/account/wishlist")
@RequiredArgsConstructor
class WishlistController {

    private final WishlistService service;

    @GetMapping
    List<ProductCardResponse> products(UserIdentity user) {
        return service.products(user.id());
    }

    @GetMapping("/product-ids")
    List<ObjectId> productIds(UserIdentity user) {
        return service.productIds(user.id());
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void add(@PathVariable ObjectId productId, UserIdentity user) {
        service.add(user.id(), productId);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void remove(@PathVariable ObjectId productId, UserIdentity user) {
        service.remove(user.id(), productId);
    }
}
