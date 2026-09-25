package com.vertyll.festival.shop.order;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;

@Mapper(config = MapStructConfig.class)
interface OrderMapper {

    OrderResponse toResponse(OrderDocument document);
}
