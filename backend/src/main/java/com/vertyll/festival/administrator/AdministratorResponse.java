package com.vertyll.festival.administrator;

import java.time.Instant;

import org.bson.types.ObjectId;

record AdministratorResponse(ObjectId id, String email, Instant createdAt) {
}
