package com.vertyll.festival.catalog.product;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

interface ProductRepository extends MongoRepository<ProductDocument, ObjectId> {
}
