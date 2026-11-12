package com.vertyll.festival.shop.order;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;
import com.vertyll.festival.shop.ShippingDetails;
import com.vertyll.festival.shop.ShippingDetailsRequest;

@Mapper(config = MapStructConfig.class)
interface OrderMapper {

    OrderResponse toResponse(OrderDocument document);

    ShippingDetails toShipping(ShippingDetailsRequest request);
}
