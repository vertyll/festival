package com.vertyll.festival.lineup.stage;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface StageMapper {

    StageResponse toResponse(StageDocument document);
}
