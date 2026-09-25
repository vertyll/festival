package com.vertyll.festival.lineup.stage;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

interface StageRepository extends MongoRepository<StageDocument, ObjectId> {
}
