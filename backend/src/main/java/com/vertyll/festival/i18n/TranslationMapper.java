package com.vertyll.festival.i18n;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface TranslationMapper {

    TranslationResponse toResponse(TranslationDocument document);
}
