package com.vertyll.festival.shop.address;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vertyll.festival.security.UserIdentity;
import com.vertyll.festival.shop.ShippingDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/account/address")
@RequiredArgsConstructor
class ShippingAddressController {

    private final ShippingAddressService service;

    @GetMapping
    ResponseEntity<ShippingDetails> get(UserIdentity user) {
        return service.find(user.id()).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping
    ShippingDetails save(@Valid @RequestBody ShippingDetails details, UserIdentity user) {
        return service.save(user.id(), details);
    }
}
