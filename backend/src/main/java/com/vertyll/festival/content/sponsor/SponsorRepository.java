package com.vertyll.festival.content.sponsor;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

interface SponsorRepository extends MongoRepository<SponsorDocument, ObjectId> {
}
