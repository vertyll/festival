package com.vertyll.festival.content.news;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface NewsMapper {

    NewsResponse toResponse(NewsDocument document);
}
