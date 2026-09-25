package com.vertyll.festival.administrator;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface AdministratorMapper {

    AdministratorResponse toResponse(AdministratorDocument document);
}
