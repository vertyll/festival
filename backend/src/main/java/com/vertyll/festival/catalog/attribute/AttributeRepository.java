package com.vertyll.festival.catalog.attribute;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

interface AttributeRepository extends MongoRepository<AttributeDocument, ObjectId> {
}
