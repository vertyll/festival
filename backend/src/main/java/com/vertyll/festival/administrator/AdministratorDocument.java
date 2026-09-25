package com.vertyll.festival.administrator;

import java.time.Instant;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("administrators")
record AdministratorDocument(
    @Id ObjectId id,
    @Indexed(unique = true) String email,
    Instant createdAt,
    Instant updatedAt
) {

    static AdministratorDocument create(String email, Instant now) {
        return new AdministratorDocument(new ObjectId(), email, now, now);
    }

    AdministratorDocument withEmail(String newEmail, Instant now) {
        return new AdministratorDocument(id, newEmail, createdAt, now);
    }
}
