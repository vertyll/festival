package com.vertyll.festival.catalog.category;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

interface CategoryRepository extends MongoRepository<CategoryDocument, ObjectId> {
}
