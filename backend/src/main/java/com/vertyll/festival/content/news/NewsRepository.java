package com.vertyll.festival.content.news;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

interface NewsRepository extends MongoRepository<NewsDocument, ObjectId> {
}
