package com.vertyll.festival.shop.address;

import org.mapstruct.Mapper;

import com.vertyll.festival.common.MapStructConfig;
import com.vertyll.festival.shop.ShippingDetails;
import com.vertyll.festival.shop.ShippingDetailsRequest;
import com.vertyll.festival.shop.ShippingDetailsResponse;

@Mapper(config = MapStructConfig.class)
interface ShippingAddressMapper {

    ShippingDetails toDetails(ShippingDetailsRequest request);

    ShippingDetailsResponse toResponse(ShippingDetails details);
}
