package com.vertyll.festival.catalog.attribute;

import java.util.List;

import org.mapstruct.Mapper;

import com.vertyll.festival.catalog.OptionValue;
import com.vertyll.festival.catalog.OptionValueRequest;
import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface AttributeMapper {

    AttributeResponse toResponse(AttributeDocument document);

    List<OptionValue> toValues(List<OptionValueRequest> values);
}
