package com.vertyll.festival.lineup.artist;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface ArtistMapper {

    ArtistResponse toResponse(ArtistDocument document);
}
