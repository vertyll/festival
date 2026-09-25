package com.vertyll.festival.shop.address;

import org.springframework.data.mongodb.repository.MongoRepository;

interface ShippingAddressRepository extends MongoRepository<ShippingAddressDocument, String> {
}
