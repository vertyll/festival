package com.vertyll.festival.content.sponsor;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface SponsorMapper {

    SponsorResponse toResponse(SponsorDocument document);
}
