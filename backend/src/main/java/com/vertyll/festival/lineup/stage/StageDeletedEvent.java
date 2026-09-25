package com.vertyll.festival.lineup.stage;

import org.bson.types.ObjectId;

public record StageDeletedEvent(ObjectId stageId) {
}
