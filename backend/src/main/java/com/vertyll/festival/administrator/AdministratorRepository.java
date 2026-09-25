package com.vertyll.festival.administrator;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

interface AdministratorRepository extends MongoRepository<AdministratorDocument, ObjectId> {

    boolean existsByEmail(String email);

    Optional<AdministratorDocument> findByEmail(String email);
}
