package com.vertyll.festival.catalog.attribute;

import java.util.List;

import org.bson.types.ObjectId;

import com.vertyll.festival.catalog.OptionValue;
import com.vertyll.festival.common.LocalizedText;

record AttributeResponse(ObjectId id, LocalizedText name, List<OptionValue> values) {
}
