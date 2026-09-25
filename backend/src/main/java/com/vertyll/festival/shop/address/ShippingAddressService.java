package com.vertyll.festival.shop.address;

import java.time.Clock;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vertyll.festival.shop.ShippingDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ShippingAddressService {

    private final ShippingAddressRepository repository;
    private final Clock clock;

    Optional<ShippingDetails> find(String customerId) {
        return repository.findById(customerId).map(ShippingAddressDocument::details);
    }

    ShippingDetails save(String customerId, ShippingDetails details) {
        return repository.save(new ShippingAddressDocument(customerId, details, clock.instant())).details();
    }
}
