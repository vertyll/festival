package com.vertyll.festival.lineup.artist;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

interface ArtistRepository extends MongoRepository<ArtistDocument, ObjectId> {
}
