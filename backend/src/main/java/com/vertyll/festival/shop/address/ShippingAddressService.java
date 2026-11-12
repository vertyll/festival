package com.vertyll.festival.shop.address;

import java.time.Clock;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.vertyll.festival.shop.ShippingDetailsRequest;
import com.vertyll.festival.shop.ShippingDetailsResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class ShippingAddressService {

    private final ShippingAddressRepository repository;
    private final ShippingAddressMapper mapper;
    private final Clock clock;

    Optional<ShippingDetailsResponse> find(String customerId) {
        return repository.findById(customerId).map(address -> mapper.toResponse(address.details()));
    }

    ShippingDetailsResponse save(String customerId, ShippingDetailsRequest request) {
        ShippingAddressDocument address =
                new ShippingAddressDocument(customerId, mapper.toDetails(request), clock.instant());
        return mapper.toResponse(repository.save(address).details());
    }
}
