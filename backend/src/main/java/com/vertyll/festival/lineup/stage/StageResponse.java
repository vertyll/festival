package com.vertyll.festival.lineup.stage;

import org.bson.types.ObjectId;

import com.vertyll.festival.common.LocalizedText;

record StageResponse(ObjectId id, LocalizedText name) {
}
