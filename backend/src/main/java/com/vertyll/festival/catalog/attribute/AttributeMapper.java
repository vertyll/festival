package com.vertyll.festival.catalog.attribute;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface AttributeMapper {

    AttributeResponse toResponse(AttributeDocument document);
}
