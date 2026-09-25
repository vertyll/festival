package com.vertyll.festival.shop.order;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

interface OrderRepository extends MongoRepository<OrderDocument, ObjectId> {

    List<OrderDocument> findByCustomerId(String customerId, Sort sort);
}
